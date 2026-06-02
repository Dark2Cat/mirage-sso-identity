package com.mirage.sso.portal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PortalService {
    private final AppMapper appMapper;

    public PortalService(AppMapper appMapper) {
        this.appMapper = appMapper;
    }

    public List<PortalAppResponse> listVisibleApps() {
        return appMapper.selectList(
                        new LambdaQueryWrapper<AppEntity>()
                                .ne(AppEntity::getStatus, AppStatus.DISABLED)
                                .orderByAsc(AppEntity::getSortOrder)
                )
                .stream()
                .map(PortalAppResponse::from)
                .toList();
    }
}
