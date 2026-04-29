package com.equip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equip.entity.DeviceStandard;
import com.equip.mapper.DeviceStandardMapper;
import com.equip.service.DeviceStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class DeviceStandardServiceImpl implements DeviceStandardService {

    @Autowired
    private DeviceStandardMapper deviceStandardMapper;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public IPage<DeviceStandard> pageQuery(int current, int size, String standard) {
        Page<DeviceStandard> page = new Page<>(current, size);
        QueryWrapper<DeviceStandard> wrapper = new QueryWrapper<>();
        if (standard != null && !standard.isEmpty()) {
            wrapper.like("name", standard).or().like("standard", standard);
        }
        wrapper.orderByDesc("create_time");
        return deviceStandardMapper.selectPage(page, wrapper);
    }

    @Override
    public void add(DeviceStandard deviceStandard) {
        String now = sdf.format(new Date());
        deviceStandard.setCreateTime(now);
        deviceStandard.setUpdateTime(now);
        deviceStandardMapper.insert(deviceStandard);
    }

    @Override
    public void update(DeviceStandard deviceStandard) {
        deviceStandard.setUpdateTime(sdf.format(new Date()));
        deviceStandardMapper.updateById(deviceStandard);
    }

    @Override
    public void delete(Long id) {
        deviceStandardMapper.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        deviceStandardMapper.deleteBatchIds(ids);
    }

    @Override
    public DeviceStandard getById(Long id) {
        return deviceStandardMapper.selectById(id);
    }

    @Override
    public List<DeviceStandard> listAll() {
        QueryWrapper<DeviceStandard> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        return deviceStandardMapper.selectList(wrapper);
    }
}