package com.equip.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.equip.entity.DeviceStandard;

import java.util.List;

public interface DeviceStandardService {

    IPage<DeviceStandard> pageQuery(int current, int size, String standard);

    void add(DeviceStandard deviceStandard);

    void update(DeviceStandard deviceStandard);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    DeviceStandard getById(Long id);

    List<DeviceStandard> listAll();
}