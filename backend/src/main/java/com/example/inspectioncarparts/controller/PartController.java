package com.example.inspectioncarparts.controller;

import com.example.inspectioncarparts.common.Page;
import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.domain.dto.BatchAssociatePartsRequest;
import com.example.inspectioncarparts.domain.entity.Part;
import com.example.inspectioncarparts.service.PartService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
@Api(tags = "配件管理")
public class PartController {
    @Autowired
    private PartService partService;

    @PostMapping
    @ApiOperation("创建配件信息")
    public Result<Part> create(@RequestBody Part part) {
        try {
            Part created = partService.createPart(part);
            return Result.success(created);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("更新配件信息")
    public Result<Part> update(@PathVariable Integer id, @RequestBody Part part) {
        part.setId(id);
        try {
            Part updated = partService.updatePart(part);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除配件信息")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            partService.deletePart(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取配件详情")
    public Result<Part> getById(@PathVariable Integer id) {
        Part part = partService.getPartById(id);
        if (part == null) {
            return Result.fail("配件不存在");
        }
        return Result.success(part);
    }

    @GetMapping
    @ApiOperation("分页查询配件信息")
    public Result<Page<Part>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<Part> parts = partService.listParts(pageNum, pageSize);
        return Result.success(parts);
    }

    @GetMapping("/tree")
    @ApiOperation("获取配件树形结构")
    public Result<List<Part>> getTree(@RequestParam(required = false) Integer rootId) {
        List<Part> tree = partService.getPartTree(rootId);
        return Result.success(tree);
    }

    @PutMapping("/{id}/associate-vehicle")
    @ApiOperation("关联配件到车辆")
    public Result<Part> associateVehicle(@PathVariable Integer id, @RequestParam Integer vehicleId) {
        try {
            Part updated = partService.associateWithVehicle(id, vehicleId);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/disassociate-vehicle")
    @ApiOperation("解除配件与车辆的关联")
    public Result<Part> disassociateVehicle(@PathVariable Integer id) {
        try {
            Part updated = partService.disassociateFromVehicle(id);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/batch-associate-vehicle")
    @ApiOperation("批量关联配件到车辆")
    public Result<List<Part>> batchAssociateVehicle(@RequestBody BatchAssociatePartsRequest request) {
        try {
            List<Part> updatedParts = partService.batchAssociateWithVehicle(request.getVehicleId(), request.getPartIds());
            return Result.success(updatedParts);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/disassociate-by-vehicle/{vehicleId}")
    @ApiOperation("根据车辆ID解除所有配件关联")
    public Result<Void> disassociateByVehicle(@PathVariable Integer vehicleId) {
        try {
            partService.disassociateAllFromVehicle(vehicleId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}/qrcode")
    @ApiOperation("生成配件二维码")
    public Result<String> generateQrCode(@PathVariable Integer id) {
        try {
            String qrCode = partService.generateQrCode(id);
            return Result.success(qrCode);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/parse-qrcode")
    @ApiOperation("解析二维码获取配件信息")
    public Result<Part> parseQrCode(@RequestParam String qrCode) {
        try {
            Part part = partService.parseQrCode(qrCode);
            return Result.success(part);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/by-vehicle-and-type")
    @ApiOperation("根据车辆ID和配件类型查询配件列表(参数可选)")
    public Result<List<Part>> listByVehicleAndType(
            @RequestParam(required = false) Integer vehicleId,
            @RequestParam(required = false) Integer typeId) {
        List<Part> parts = partService.listByVehicleAndType(vehicleId, typeId);
        return Result.success(parts);
    }

    @GetMapping("/qr-code/{partCode}")
    @ApiOperation("生成配件二维码")
    public void generateQRCode(@PathVariable String partCode,
                              HttpServletResponse response) throws Exception {
        // 1. 构建详情页URL
        String detailUrl = "http://192.168.54.207:3001/parts/detail/" + partCode;

        // 2. 生成二维码配置
        int width = 300;
        int height = 300;
        String format = "png";

        // 3. 生成二维码图片
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(detailUrl, BarcodeFormat.QR_CODE, width, height);

        // 4. 输出到响应流
        response.setContentType("image/" + format);
        ServletOutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, format, outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @GetMapping("/detail/{partCode}")
    @ApiOperation("获取配件详情")
    public Result<Part> getPartDetail(@PathVariable String partCode) {
        Part part = partService.getPartByCode(partCode);
        return Result.success(part);
    }


}