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

package com.pnoker.common.dto.transfer;

import com.pnoker.common.base.Converter;
import com.pnoker.common.bean.PageInfo;
import com.pnoker.common.entity.rtmp.Rtmp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

/**
 * <p>Rtmp DTO
 *
 * @author : pnoker
 * @email : pnokers@icloud.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class RtmpDto extends Rtmp implements Converter<Rtmp> {

    private PageInfo page;

    public RtmpDto(boolean autoStart) {
        super(autoStart);
    }

    @Override
    public void convertToDo(Rtmp rtmp) {
        BeanUtils.copyProperties(this, rtmp);
    }

    @Override
    public void convertToDto(Rtmp rtmp) {
        BeanUtils.copyProperties(rtmp, this);
    }
}