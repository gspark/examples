package com.shrill;

import com.shrill.dao.EvalResDao;
import com.shrill.dao.impl.EvalResDaoImpl;
import com.shrill.pojo.EvalRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        EvalResDao evalResDao = new EvalResDaoImpl();

        List<EvalRes> resList = evalResDao.findAll();

        for (EvalRes res : resList) {
            log.info(res.getData_dt());
        }
    }
}
