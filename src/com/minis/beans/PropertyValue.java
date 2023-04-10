package com.minis.beans;

public class PropertyValue {
	private final String type;
	private final String name;
	//value定义为object类型，因为value的值类型可能很多，不同的基本数据类型都不同
	private final Object value;
	//判断是否为引用
	private final boolean isRef;

	public PropertyValue(String type, String name, Object value, boolean isRef) {
		this.type = type;
		this.name = name;
		this.value = value;
		this.isRef = isRef;
	}

	public String getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		return this.value;
	}

	public boolean getIsRef() {
		return isRef;
	}
}

