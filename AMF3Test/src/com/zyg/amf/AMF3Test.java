package com.zyg.amf;

import flex.messaging.io.ClassAliasRegistry;
import flex.messaging.io.MessageDeserializer;
import flex.messaging.io.amf.ActionContext;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.AmfMessageDeserializer;
import flex.messaging.io.amf.AmfTrace;
import flex.messaging.validators.ClassDeserializationValidator;
import flex.messaging.validators.DeserializationValidator;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.Amf3Input;

public class AMF3Test {
    public static void main(String[] args) {

        ClassAliasRegistry.getRegistry().registerAlias("DSK","flex.messaging.messages.AcknowledgeMessageExt");
//		ClassAliasRegistry.getRegistry().registerAlias("com.greencloud.dto.UserDto","com.greencloud.dto.UserDto");

        // 序列化
//		SerializationContext sercontext = new SerializationContext();
        SerializationContext sercontext = SerializationContext.getSerializationContext();
        ClassDeserializationValidator validator = new ClassDeserializationValidator();
        validator.addAllowClassPattern("com.greencloud.dto.UserDto");
        validator.addAllowClassPattern("com.greencloud.entity.App");
        validator.addAllowClassPattern("com.greencloud.entity.Hotel");
        validator.addAllowClassPattern("com.greencloud.entity.HotelGroup");
        validator.addAllowClassPattern("com.greencloud.entity.User");
        validator.addAllowClassPattern("com.greencloud.entity.WorkStation");

        sercontext.setDeserializationValidator(validator);

        Amf3Input amfinput = new Amf3Input(sercontext);
        try {
            long time1 = System.currentTimeMillis();
            int times = 10;
            //测试性能
            for(int i=0;i<times;i++)
            {
                FileInputStream fos = new FileInputStream("loginAppById.amf");
                DataInputStream dos = new DataInputStream(fos);

                AmfTrace trace = new AmfTrace();
                MessageDeserializer deserializer = new AmfMessageDeserializer();
                deserializer.initialize(sercontext,dos,trace);
                ActionContext context = new ActionContext();

                ActionMessage message = new ActionMessage();
                deserializer.readMessage(message, context);

                amfinput.setInputStream(dos);
                HashMap<?, ?> map = (HashMap<?, ?>) amfinput.readObject();

                Iterator<?> iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    System.out.println("key="+key+",value="+val);
                }
            }
            System.out.println("\ncost time="+(System.currentTimeMillis() - time1));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
