/*
 * Copyright 2019 Pnoker. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pnoker.center.device.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pnoker.common.bean.Pages;
import com.pnoker.common.constant.Common;
import com.pnoker.common.dto.PointDto;
import com.pnoker.common.exception.ServiceException;
import com.pnoker.common.model.Dic;
import com.pnoker.common.model.Point;
import com.pnoker.center.device.mapper.PointMapper;
import com.pnoker.center.device.service.PointService;
import com.pnoker.center.device.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>位号服务接口实现类
 *
 * @author pnoker
 */
@Slf4j
@Service
public class PointServiceImpl implements PointService {

    @Resource
    private ProfileService profileService;

    @Resource
    private PointMapper pointMapper;

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.POINT_ID, key = "#point.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.POINT_NAME, key = "#point.name+'.'+#point.name", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.POINT_DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public Point add(Point point) {
        Point select = selectByNameAndProfile(point.getProfileId(), point.getName());
        if (null != select) {
            throw new ServiceException("point already exists in the profile");
        }
        if (pointMapper.insert(point) > 0) {
            return pointMapper.selectById(point.getId());
        }
        return null;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.POINT_ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_NAME, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        return pointMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.POINT_ID, key = "#point.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.POINT_NAME, key = "#point.profileId+'.'+#point.name", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.POINT_DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public Point update(Point point) {
        point.setUpdateTime(null);
        Point selectById = pointMapper.selectById(point.getId());
        if (!selectById.getProfileId().equals(point.getProfileId()) || !selectById.getName().equals(point.getName())) {
            Point select = selectByNameAndProfile(point.getProfileId(), point.getName());
            if (null != select) {
                throw new ServiceException("point already exists");
            }
        }
        if (pointMapper.updateById(point) > 0) {
            Point select = selectById(point.getId());
            point.setName(select.getName());
            return select;
        }
        return null;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_ID, key = "#id", unless = "#result==null")
    public Point selectById(Long id) {
        return pointMapper.selectById(id);
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_NAME, key = "#profileId+'.'+#name", unless = "#result==null")
    public Point selectByNameAndProfile(Long profileId, String name) {
        LambdaQueryWrapper<Point> queryWrapper = Wrappers.<Point>query().lambda();
        queryWrapper.eq(Point::getName, name);
        queryWrapper.eq(Point::getProfileId, profileId);
        return pointMapper.selectOne(queryWrapper);
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<Point> list(PointDto pointDto) {
        if (!Optional.ofNullable(pointDto.getPage()).isPresent()) {
            pointDto.setPage(new Pages());
        }
        return pointMapper.selectPage(pointDto.getPage().convert(), fuzzyQuery(pointDto));
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_DIC, key = "'point_dic'", unless = "#result==null")
    public List<Dic> dictionary() {
        List<Dic> profileDicList = profileService.dictionary();
        for (Dic driverDic : profileDicList) {
            for (Dic profileDic : driverDic.getChildren()) {
                List<Dic> dicList = new ArrayList<>();
                LambdaQueryWrapper<Point> queryWrapper = Wrappers.<Point>query().lambda();
                queryWrapper.eq(Point::getProfileId, profileDic.getValue());
                List<Point> pointList = pointMapper.selectList(queryWrapper);
                profileDic.setDisabled(true);
                profileDic.setValue(RandomUtil.randomLong());
                for (Point point : pointList) {
                    Dic pointDic = new Dic().setLabel(point.getName()).setValue(point.getId());
                    dicList.add(pointDic);
                }
                profileDic.setChildren(dicList);
            }
            driverDic.setDisabled(true);
            driverDic.setValue(RandomUtil.randomLong());
        }
        return profileDicList;
    }

    @Override
    public LambdaQueryWrapper<Point> fuzzyQuery(PointDto pointDto) {
        LambdaQueryWrapper<Point> queryWrapper = Wrappers.<Point>query().lambda();
        Optional.ofNullable(pointDto).ifPresent(dto -> {
            if (StringUtils.isNotBlank(dto.getName())) {
                queryWrapper.like(Point::getName, dto.getName());
            }
            if (StringUtils.isNotBlank(dto.getType())) {
                queryWrapper.eq(Point::getType, dto.getType());
            }
            if (null != dto.getProfileId()) {
                queryWrapper.eq(Point::getProfileId, dto.getProfileId());
            }
            if (null != dto.getRw()) {
                queryWrapper.eq(Point::getRw, dto.getRw());
            }
            if (null != dto.getAccrue()) {
                queryWrapper.eq(Point::getAccrue, dto.getAccrue());
            }
        });
        return queryWrapper;
    }

}
