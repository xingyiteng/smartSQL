package com.iteng.startup.model.enums;

/**
 * @author iteng
 * @date 2024-02-02 18:57
 */
public enum DemoEnum {
    FILE(0, "文件"),
    FOLDER(1, "目录");

    private final Integer value;
    private final String desc;

    DemoEnum(Integer value, String desc) {
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
     * 根据value获取DemoEnum实例
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
