package application.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<T> {

    private T content;

    public Response(T content) {
        this.content = content;
    }

}
