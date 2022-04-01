package softuni.exam.instagraphlite.models.DTO;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class UserUserNameDto {

    @XmlElement
    private String username;

    @Size(min = 2,max = 18)
    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }
}
