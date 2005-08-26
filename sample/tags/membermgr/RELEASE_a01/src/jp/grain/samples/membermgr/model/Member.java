/*
 * Member manager - Grain Sample Code
 * Copyright (C) 2005 HAW International Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Created on 2005/08/25 13:57:45
 * 
 */
package jp.grain.samples.membermgr.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Member
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class Member {

    private static final Logger log = Logger.getLogger(Member.class);
    private static Map memberMap = new HashMap();
        
    private String id;
    private String name;
    private String postCode;
    private String address;
    private String building;
    private String mail;
    private String age;
    private String gender;
    private String jobCode;
    private String result;

    /**
     * 
     */
    public Member(String id) {
        this.id = id;
    }

    /**
     * 
     */
    public Member() {        
    }

    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getAge() {
        return age;
    }
    
    public void setAge(String age) {
        this.age = age;
    }
    
    public String getBuilding() {
        return building;
    }
    
    public void setBuilding(String building) {
        this.building = building;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getJobCode() {
        return jobCode;
    }
    
    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }
    
    public String getMail() {
        return mail;
    }
    
    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPostCode() {
        return postCode;
    }
    
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
    
    /**
     * @param member
     * @return
     */
    public static void addMember(Member member) {
        if (member.id == null || member.id.length() == 0) {
            member.result = "IDÇ™éwíËÇ≥ÇÍÇƒÇ¢Ç‹ÇπÇÒÅB";
            return;
        } else if (memberMap.containsKey(member.id)) {
            member.result = "ìØÇ∂IDÇ™ä˘Ç…ìoò^Ç≥ÇÍÇƒÇ¢Ç‹Ç∑ÅB";
            return;            
        }
        memberMap.put(member.id, member);
        member.result = "ìoò^ÇµÇ‹ÇµÇΩÅB";
        log.info("add member : [" + member.id + "] = " + member.getName());
    }

    public static Member createMemberByXMLDocument(Document doc) throws InstantiationException {
        Element memberElem = doc.getRootElement();
        if (!"member".equals(memberElem.getName())) {
            log.error("no member data: " + memberElem.getName());
            throw new InstantiationException("no member data");
        }        
        Member member = new Member(memberElem.getChildTextNormalize("id"));
        member.setName(memberElem.getChildTextNormalize("name"));
        member.setPostCode(memberElem.getChildTextNormalize("post-code"));
        member.setAddress(memberElem.getChildTextNormalize("address"));
        member.setBuilding(memberElem.getChildTextNormalize("building"));
        member.setMail(memberElem.getChildTextNormalize("mail"));
        member.setAge(memberElem.getChildTextNormalize("age"));
        member.setGender(memberElem.getChildTextNormalize("gender"));
        member.setJobCode(memberElem.getChildTextNormalize("job-code"));
        return member;
    }
    
    /**
     * @return
     */
    public Document toXMLDocument() {
        Element member = new Element("member");
        member.addContent(new Element("id").setText(this.id));
        member.addContent(new Element("name").setText(this.name));
        member.addContent(new Element("post-code").setText(this.postCode));
        member.addContent(new Element("address").setText(this.address));
        member.addContent(new Element("building").setText(this.building));
        member.addContent(new Element("mail").setText(this.mail));
        member.addContent(new Element("age").setText(this.age));
        member.addContent(new Element("gender").setText(this.gender));
        member.addContent(new Element("job-code").setText(this.jobCode));
        member.addContent(new Element("result").setText(this.result));
        return new Document().setRootElement(member);
    }

}
