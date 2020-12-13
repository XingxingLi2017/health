package com.xing.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xing.dao.AccessKeysDao;
import com.xing.service.AccessKeysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service(interfaceClass = com.xing.service.AccessKeysService.class)
@Transactional
public class AccessKeysServiceImpl implements AccessKeysService {

    @Autowired
    AccessKeysDao accessKeysDao;

    @Override
    public Map<String, Object> getMailKey() {
        return accessKeysDao.getMailKey();
    }
}
