package com.shrill;

import com.shrill.dao.EvalResDao;
import com.shrill.dao.impl.EvalResDaoImpl;
import com.shrill.example.RandomTest;
import com.shrill.netty.TelnetClient;
import com.shrill.pojo.EvalRes;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
//        testDao();
//         testSSHClient();
        invokeSameSeed1();
    }

    private static void testDao() {
        EvalResDao evalResDao = new EvalResDaoImpl();

        List<EvalRes> resList = evalResDao.findAll();

        for (EvalRes res : resList) {
            log.info(res.getData_dt());
        }
    }

    public static void testSSHClient() {
        TelnetClient tc = new TelnetClient("192.168.251.175", 22, true);
        tc.connect();
    }

    public static void invokeSameSeed1() {
        try {
            Method method = RandomTest.class.getDeclaredMethod("sameSeed");
            method.setAccessible(true);
            method.invoke(null);
        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException
            | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
