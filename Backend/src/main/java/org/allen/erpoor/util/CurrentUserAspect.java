package org.allen.erpoor.util;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CurrentUserAspect {

    @PersistenceContext
    private EntityManager em;

    @Before("@annotation(org.allen.erpoor.util.SetAppUser)")
    public void setAppCurrentUser() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        em.createNativeQuery("SELECT set_config('app.current_user', :user, true)")
                .setParameter("user", currentUser)
                .getSingleResult();
    }
}
