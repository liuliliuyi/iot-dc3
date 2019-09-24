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

package com.pnoker.center.dbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pnoker.center.dbs.mapper.RtmpMapper;
import com.pnoker.center.dbs.service.RtmpService;
import com.pnoker.common.model.domain.rtmp.Rtmp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>Copyright(c) 2019. Pnoker All Rights Reserved.
 * <p>@Author    : Pnoker
 * <p>Email      : pnokers@gmail.com
 * <p>Description: Rtmp 接口实现
 */
@Slf4j
@Service
public class RtmpServiceImpl implements RtmpService {
    @Autowired
    private RtmpMapper rtmpMapper;

    @Override
    @CachePut(value = "rtmp", key = "#rtmp.id")
    public Rtmp add(Rtmp rtmp) {
        rtmpMapper.insert(rtmp);
        return rtmp;
    }

    @Override
    @CacheEvict(value = "rtmp", key = "#rtmp.id")
    public boolean delete(long id) {
        return rtmpMapper.deleteById(id) > 0 ? true : false;
    }

    @Override
    @CachePut(value = "rtmp", key = "#rtmp.id")
    public Rtmp update(Rtmp rtmp) {
        rtmpMapper.updateById(rtmp);
        return rtmp;
    }

    @Override
    public List<Rtmp> list(Rtmp rtmp) {
        QueryWrapper<Rtmp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("auto_start", rtmp.isAutoStart());
        queryWrapper.like("name", rtmp.getName());
        return rtmpMapper.selectList(queryWrapper);
    }

    @Override
    @Cacheable(value = "rtmp", key = "#rtmp.id", unless = "#result == null")
    public Rtmp selectById(long id) {
        return rtmpMapper.selectById(id);
    }
}
