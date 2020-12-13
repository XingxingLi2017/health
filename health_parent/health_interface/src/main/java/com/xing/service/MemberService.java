package com.xing.service;

import com.xing.pojo.Member;

import java.util.List;

public interface MemberService {
    public Member findByEmail(String email);
    public void add(Member member);
    public List<Integer> findMemberCountByMonths(List<String> months);
}
