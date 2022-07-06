package auth.integrationauth.controller.dto.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoProfile {

    private long id;
    private String connected_at;
    private Properties properties;
    private KakaoAccount kakao_account;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Properties{
        private String nickname;
        private String profile_image;
        private String thumbnail_image;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class KakaoAccount{
        private boolean profile_nickname_needs_agreement;
        private boolean profile_image_needs_agreement;
        private Profile profile;
        private boolean has_email;
        private boolean email_needs_agreement;
        private boolean is_email_valid;
        private boolean is_email_verified;
        private String email;
        
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public class Profile{
            private String nickname;
            private String thumbnail_image_url;
            private String profile_image_url;
            private boolean is_default_image;
        }
    }

}
