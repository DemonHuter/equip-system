package com.equip.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.equip.common.Response;
import com.equip.entity.BatchDeleteRequest;
import com.equip.entity.DeviceStandard;
import com.equip.service.DeviceStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standard")
public class DeviceStandardController {

    @Autowired
    private DeviceStandardService deviceStandardService;

    @GetMapping("/list")
    public Response<IPage<DeviceStandard>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String standard) {
        IPage<DeviceStandard> page = deviceStandardService.pageQuery(current, size, standard);
        return Response.success(page);
    }

    @GetMapping("/get/{id}")
    public Response<DeviceStandard> getById(@PathVariable Long id) {
        DeviceStandard deviceStandard = deviceStandardService.getById(id);
        return Response.success(deviceStandard);
    }

    @GetMapping("/all")
    public Response<List<DeviceStandard>> listAll() {
        List<DeviceStandard> list = deviceStandardService.listAll();
        return Response.success(list);
    }

    @PostMapping("/add")
    public Response<Void> add(@RequestBody DeviceStandard deviceStandard) {
        deviceStandardService.add(deviceStandard);
        return Response.success();
    }

    @PutMapping("/update")
    public Response<Void> update(@RequestBody DeviceStandard deviceStandard) {
        deviceStandardService.update(deviceStandard);
        return Response.success();
    }

    @DeleteMapping("/delete/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        deviceStandardService.delete(id);
        return Response.success();
    }

    @PostMapping("/batchDelete")
    public Response<Void> batchDelete(@RequestBody BatchDeleteRequest request) {
        deviceStandardService.batchDelete(request.getIds());
        return Response.success();
    }
}