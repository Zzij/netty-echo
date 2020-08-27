package com.zz.chaobai.msgpack;

import com.zz.chaobai.vo.Member;
import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.template.Templates;
import org.msgpack.type.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Demo1 {
    public static void main(String[] args) {
        List<Member> list = new ArrayList<Member>();
        for (int i = 0; i < 10; i++) {
            Member member = new Member();
            member.setName("mem-" + i);
            member.setAge(10);
            member.setSalary(i);
            list.add(member);
        }
        System.out.println(list.toString().getBytes().length);
        MessagePack msgPack = new MessagePack();
        try {
            byte[] write = msgPack.write(list);
            System.out.println(write.length);
            List<Member> read = msgPack.read(write, Templates.tList(msgPack.lookup(Member.class)));
            System.out.println(read.toString());
        } catch (IOException e) {

        }
    }
}
