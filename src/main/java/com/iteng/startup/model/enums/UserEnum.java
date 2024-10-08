package com.iteng.startup.model.enums;

/**
 * @author iteng
 * @date 2024-02-10 16:06
 */
public enum UserEnum {
    ACTIVATE(0, "正常"),
    DISABLE(1, "禁用");

    private final Integer value;

    private final String desc;

    UserEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据value获取enum实例
     */
    public static DemoEnum getByValue(Integer value) {
        for (DemoEnum item : DemoEnum.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
