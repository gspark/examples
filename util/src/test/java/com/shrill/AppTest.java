package com.shrill;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.shrill.netty.TelnetClient;
import com.shrill.util.PropertiesHelper;
import com.sun.org.apache.bcel.internal.generic.FSTORE;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

import java.io.*;
import java.util.*;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testPropertiesHelper() {
        assertEquals("2", PropertiesHelper.getProperty("db2.url"));
    }

    @Test
    public void testHashMap() throws Exception {
//        HashMap<String, String> hmap = new HashMap<String, String>() {{
//            put(new String("key"), new String("value"));
//        }};
        Map<String, Object> hmap = new HashMap<>();
        hmap.put(new String("key"), new String("value"));

        hmap.put("a", "1");
        hmap.put("b", new byte[]{0x00,(byte) 0x99,(byte) 0xef,0x66});

        Map<String, Object> subMap = new HashMap<String, Object>();
        subMap.put("suba", "1".getBytes());

        hmap.put("subMap", subMap);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(subMap);
        list.add(subMap);
        hmap.put("list", list);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        out = new ObjectOutputStream(bos);
        out.writeObject(hmap);
        byte[] yourBytes = bos.toByteArray();
        out.close();
        bos.close();

        System.out.println("size:" + yourBytes.length);

        ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
        ObjectInput in = null;
        in = new ObjectInputStream(bis);
        HashMap<String, Object> o = (HashMap<String, Object>)in.readObject();
        bis.close();
        in.close();

//        assertEquals(hmap, o);
    }

    @Test
    public void testKryoHashMap() throws Exception {
        Map<String, Object> hmap = new HashMap<>();
        hmap.put(new String("key"), new String("value"));

        hmap.put("a", "1");
        hmap.put("b", new byte[]{0x00,(byte) 0x99,(byte) 0xef,0x66});

        Map<String, Object> subMap = new HashMap<String, Object>();
        subMap.put("suba", "1".getBytes());

        hmap.put("subMap", subMap);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(subMap);
        list.add(subMap);
        hmap.put("list", list);

        Kryo kryo = new Kryo();
//        MapSerializer serializer = new MapSerializer();
        kryo.register(HashMap.class);
        kryo.register(byte[].class);
        kryo.register(ArrayList.class);
//        kryo.register(LinkedHashMap.class, serializer);
//        kryo.register(ArrayList.class, serializer);
//        kryo.register(byte[].class, serializer);
//        serializer.setKeyClass(String.class, kryo.getSerializer(String.class));
//        serializer.setKeysCanBeNull(false);

//        Output output = new Output(1024, -1);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);

        kryo.writeObject(output, hmap);

        byte[] yourBytes = output.toBytes();
        output.close();
        bos.close();

        System.out.println("size:" + yourBytes.length);

        Input input = new Input(yourBytes);
        HashMap object2 = kryo.readObject(input, HashMap.class);

        assertEquals(hmap, object2);
    }

    @Test
    public void testVersion() {
        String version = "1.0.0.9-202033333";
        String ver = version.substring(0,version.indexOf("-"));
        System.out.println(ver);
        assertEquals("1.0.0.9", ver);
    }
}
