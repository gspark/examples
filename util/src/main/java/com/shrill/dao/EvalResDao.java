package com.shrill.dao;


import com.shrill.pojo.EvalRes;

import java.util.List;

public interface EvalResDao {
    int update();
    List<EvalRes> findAll();
}
