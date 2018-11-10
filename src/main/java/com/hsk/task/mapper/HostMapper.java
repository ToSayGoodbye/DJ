package com.hsk.task.mapper;

import com.hsk.task.bean.Host;

public interface HostMapper {
    Host selectByPhone(String phone);
}