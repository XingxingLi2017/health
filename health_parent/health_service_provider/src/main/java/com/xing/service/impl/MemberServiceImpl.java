package com.xing.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xing.dao.MemberDao;
import com.xing.pojo.Member;
import com.xing.service.MemberService;
import com.xing.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = com.xing.service.MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Override
    public Member findByEmail(String email) {
        Member m = memberDao.findByEmail(email);
        return m;
    }

    @Override
    public void add(Member member) {
        String password = member.getPassword();
        if(password != null) {
            // encrypt password by MD5 algorithm
            password = MD5Utils.md5(member.getPassword());
            member.setPassword(password);
        }
        memberDao.add(member);
    }

    @Override
    public List<Integer> findMemberCountByMonths(List<String> months) {
        List<Integer> list = new ArrayList<>();
        for(String month : months) {
            // month = yyyy.MM
            month += ".31";
            list.add(memberDao.findMemberCountBeforeDate(month));
        }
        return list;
    }
}
