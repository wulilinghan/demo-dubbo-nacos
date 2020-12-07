package cn.tlh.ex.common.vo.resp;


import cn.tlh.ex.common.exception.BusinessMsgEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 统一返回对象
 *
 * @param <T>
 * @author ling
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("all")
public class Response<T> {

    private T data;
    private String code;
    private String msg;

    // ------------------ 成功模板 ------------------------ //
    public static Response ok() {
        return new Response("", "0", "操作成功！");
    }

    public static Response ok(String code, String msg) {
        return new Response("", code, msg);
    }

    public static <T> Response ok(T data) {
        return new Response(data, "0", "操作成功！");
    }

    public static <T> Response ok(T data, String code, String msg) {
        return new Response(data, code, msg);
    }

    // ------------------ 失败模板 ------------------------ //
    public static Response fail(String message) {
        return new Response("", "-1", message);
    }

    public static Response fail(String code, String message) {
        return new Response("", code, message);
    }

    public static <T> Response fail(T data, String code, String msg) {
        return new Response(data, code, msg);
    }

    /**
     * 使用自定义异常作为参数传递状态码和提示信息
     *
     * @param msgEnum
     */
    public static Response fail(BusinessMsgEnum msgEnum) {
        return new Response("", msgEnum.code(), msgEnum.msg());
    }

}
