package softuni.exam.instagraphlite.util;

import org.hibernate.mapping.Constraint;

import javax.swing.*;
import javax.validation.ConstraintViolation;
import java.util.Set;

public interface ValidationUtil {
    <T> boolean isValid(T entity);


    //НЕ Е ЗА ИЗПИТА !!!
//    <T> Set<ConstraintViolation<T>> violation(T entity);
}
