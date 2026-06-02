package com.mirage.sso.audit;

import com.mirage.sso.common.ApiResponse;
import com.mirage.sso.common.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AuditLogAdminController {
    private final AuditLogService auditLogService;

    public AuditLogAdminController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ApiResponse<PageResponse<AuditLogResponse>> auditLogs(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) AuditResult result,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.success(auditLogService.pageAuditLogs(page, size, eventType, result, keyword));
    }
}
