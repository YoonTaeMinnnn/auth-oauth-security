package auth.integrationauth.repository.user;

import auth.integrationauth.domain.Member;
import auth.integrationauth.domain.OauthType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    public Optional<Member> findByLoginId(String loginId) {
        return em.createQuery("select m from Member m where m.loginId = :loginId", Member.class)
                .setParameter("loginId", loginId)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Optional<Member> findByKakaoEmail(String email) {
        return em.createQuery("select m from Member m where m.oauthType = :oauthType and m.email = :email", Member.class)
                .setParameter("oauthType", OauthType.KAKAO)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Optional<Member> findByLoginIdForKakao(String loginId) {
        return em.createQuery("select m from Member m where m.oauthType = :normal", Member.class)
                .setParameter("normal", OauthType.NORMAL)
                .getResultList()
                .stream()
                .findFirst();
    }






}
