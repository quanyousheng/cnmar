package com.example.administrator.cnmar.model;

import com.alibaba.fastjson.annotation.JSONField;

/** 供应商 */
public class Supply extends BaseModel {

	@JSONField(ordinal = 1)
	private String code; // 供应商编码
	@JSONField(ordinal = 2)
	private String name; // 供应商名称
	@JSONField(serialize = false)
	private String tel; // 电话
	@JSONField(serialize = false)
	private String fax; // 传真
	@JSONField(serialize = false)
	private String address; // 地址
	@JSONField(serialize = false)
	private String intro; // 简介
	@JSONField(serialize = false)
	private String contact; // 联系人
	@JSONField(serialize = false)
	private String job; // 职位
	@JSONField(serialize = false)
	private String phone; // 手机号
	@JSONField(serialize = false)
	private String email; // 邮箱

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}