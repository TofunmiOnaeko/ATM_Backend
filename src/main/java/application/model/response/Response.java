package application.model.response;

import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatusCode;

@Getter
@Setter
public class Response<T> {

    private T content;

    public Response(T content) {
        this.content = content;
    }

}
