package com.shrill.dao.impl;


import com.shrill.dao.BaseDao;
import com.shrill.dao.EvalResDao;
import com.shrill.pojo.EvalRes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvalResDaoImpl extends BaseDao implements EvalResDao {

    @Override
    public int update() {
        String sql="update user set id=?,name=? where id=?";
//        Object[] objects={User.getId(),User.getName(),User.getId()};
//        return executeUpdate(sql,objects);
        return 0;
    }

    @Override
    public List<EvalRes> findAll() {
        String sql="select * from yxkj_eval_res_list";
        ResultSet rs=getRs(sql);
        List<EvalRes> userList=new ArrayList<EvalRes>();
        try {
            while(rs.next()){
                userList.add(set(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            close();
        }
        return userList;
    }

    /**
     * 设置对象的值
     * @param set
     * @return
     */
    public EvalRes set(ResultSet set){
        EvalRes evalRes = new EvalRes();
        try {
            evalRes.setData_dt(set.getString(1));
            evalRes.setGrp_rank(set.getString(5));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evalRes;
    }
}
