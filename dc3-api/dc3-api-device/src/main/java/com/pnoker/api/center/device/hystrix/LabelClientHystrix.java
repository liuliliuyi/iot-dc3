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

package com.pnoker.api.center.device.hystrix;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pnoker.api.center.device.feign.LabelClient;
import com.pnoker.common.bean.R;
import com.pnoker.common.dto.LabelDto;
import com.pnoker.common.model.Label;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>设备 FeignHystrix
 *
 * @author pnoker
 */
@Slf4j
@Component
public class LabelClientHystrix implements FallbackFactory<LabelClient> {

    @Override
    public LabelClient create(Throwable throwable) {
        String message = throwable.getMessage() == null ? "No available server for client: DC3-MANAGER" : throwable.getMessage();
        log.error("LabelClientHystrix:{},hystrix服务降级处理", message, throwable);

        return new LabelClient() {

            @Override
            public R<Label> add(Label label) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> delete(Long id) {
                return R.fail(message);
            }

            @Override
            public R<Label> update(Label label) {
                return R.fail(message);
            }

            @Override
            public R<Label> selectById(Long id) {
                return R.fail(message);
            }

            @Override
            public R<Label> selectByName(String name) {
                return R.fail(message);
            }

            @Override
            public R<Page<Label>> list(LabelDto labelDto) {
                return R.fail(message);
            }
        };
    }
}