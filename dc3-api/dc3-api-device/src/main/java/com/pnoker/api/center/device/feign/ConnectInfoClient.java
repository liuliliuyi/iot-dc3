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

package com.pnoker.api.center.device.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pnoker.api.center.device.hystrix.ConnectInfoClientHystrix;
import com.pnoker.common.bean.R;
import com.pnoker.common.constant.Common;
import com.pnoker.common.dto.ConnectInfoDto;
import com.pnoker.common.model.ConnectInfo;
import com.pnoker.common.valid.Insert;
import com.pnoker.common.valid.Update;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>驱动连接配置信息 FeignClient
 *
 * @author pnoker
 */
@FeignClient(path = Common.Service.DC3_DEVICE_CONNECT_INFO_URL_PREFIX, name = Common.Service.DC3_DEVICE, fallbackFactory = ConnectInfoClientHystrix.class)
public interface ConnectInfoClient {

    /**
     * 新增 ConnectInfo 记录
     *
     * @param connectInfo
     * @return ConnectInfo
     */
    @PostMapping("/add")
    R<ConnectInfo> add(@Validated(Insert.class) @RequestBody ConnectInfo connectInfo);

    /**
     * 根据 ID 删除 ConnectInfo
     *
     * @param id connectInfoId
     * @return Boolean
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@PathVariable(value = "id") Long id);

    /**
     * 修改 ConnectInfo 记录
     *
     * @param connectInfo
     * @return ConnectInfo
     */
    @PostMapping("/update")
    R<ConnectInfo> update(@Validated(Update.class) @RequestBody ConnectInfo connectInfo);

    /**
     * 根据 ID 查询 ConnectInfo
     *
     * @param id
     * @return ConnectInfo
     */
    @GetMapping("/id/{id}")
    R<ConnectInfo> selectById(@PathVariable(value = "id") Long id);

    /**
     * 根据 Name 查询 ConnectInfo
     *
     * @param name
     * @return ConnectInfo
     */
    @GetMapping("/name/{name}")
    R<ConnectInfo> selectByName(@PathVariable(value = "name") String name);

    /**
     * 分页查询 ConnectInfo
     *
     * @param connectInfoDto
     * @return Page<ConnectInfo>
     */
    @PostMapping("/list")
    R<Page<ConnectInfo>> list(@RequestBody(required = false) ConnectInfoDto connectInfoDto);

}
