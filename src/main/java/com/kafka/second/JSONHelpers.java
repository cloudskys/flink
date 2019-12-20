package com.kafka.second;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description: ����ԭʼ��Ϣ�ĸ�����
 * @author: willzhao E-mail: zq2599@gmail.com
 * @date: 2019/1/1 20:13
 */
public class JSONHelpers {

    /**
     * ������Ϣ���õ�ʱ���ֶ�
     * @param raw
     * @return
     */
    public static long getTimeLongFromRawMessage(String raw){
        SingleMessage singleMessage = parse(raw);
        return null==singleMessage ? 0L : singleMessage.getTimeLong();
    }

    /**
     * ����Ϣ�����ɶ���
     * @param raw
     * @return
     */
    public static SingleMessage parse(String raw){
        SingleMessage singleMessage = null;

        if (raw != null) {
            singleMessage = JSONObject.parseObject(raw, SingleMessage.class);
        }

        return singleMessage;
    }
}
