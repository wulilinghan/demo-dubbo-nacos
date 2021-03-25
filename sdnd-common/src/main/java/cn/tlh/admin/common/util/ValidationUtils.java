
package cn.tlh.admin.common.util;

import cn.hutool.core.util.ObjectUtil;
import cn.tlh.admin.common.exception.myexception.BusinessErrorException;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

/**
 * 验证工具
 *
 * @author TANG
 * @date 2020-11-23
 */
public class ValidationUtils {

    /**
     * 验证空
     */
    public static void isNull(Object obj, String entity, String parameter, Object value) {
        if (ObjectUtil.isNull(obj)) {
            String msg = entity + " 不存在: " + parameter + " is " + value;
            throw new BusinessErrorException(msg);
        }
    }

    /**
     * 验证是否为邮箱
     */
    public static boolean isEmail(String email) {
        return new EmailValidator().isValid(email, null);
    }
}