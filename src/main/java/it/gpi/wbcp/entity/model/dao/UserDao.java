/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.gpi.wbcp.entity.model.dao;

import it.gpi.wbcp.entity.mapper.MapStruct;
import it.gpi.wbcp.entity.model.entity.dto.User;
import it.gpi.wbcp.entity.model.entity.ejb.UserEjb;
import it.gpi.wbcp.util.StringUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
public class UserDao {

    private static final Logger logger = LogManager.getLogger();

    @PersistenceContext(unitName="WBCP_PU")
    private EntityManager em;

    public User persist(User user) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        UserEjb userEjb = MapStruct.INSTANCE.userToUserEjb(user);
    	  userEjb.setUpdateInstantUTC(Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK));
    	  userEjb.setUpdateInstantLocale(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));
        em.persist(userEjb);

        User response = MapStruct.INSTANCE.userEjbToUser(userEjb);
        return response;
    }

    public boolean verifyCredentials(String email, String password) {
        boolean response = false;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<UserEjb> q = cb.createQuery(UserEjb.class);
            Root<UserEjb> r = q.from(UserEjb.class);
            q.select(r).where(cb.equal(r.<String>get("email"), email));
            TypedQuery<UserEjb> tq = em.createQuery(q);
            UserEjb u = tq.getSingleResult();
            String dbPasswordHashBase64 = u.getPasswordHashBase64();
            String requestPasswordHashBase64 = StringUtil.getSHA512Base64(password);
            if (requestPasswordHashBase64.equals(dbPasswordHashBase64)) {
                response = true;
            }
        } catch (NoResultException nre) {
            logger.info("Login refused: not found User with email: |{}|", email);
        }
        return response;
    }

    public User getByEmail(String email) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	User response = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<UserEjb> q = cb.createQuery(UserEjb.class);
            Root<UserEjb> r = q.from(UserEjb.class);
            q.select(r).where(cb.equal(r.<String>get("email"), email));
            TypedQuery<UserEjb> tq = em.createQuery(q);
            UserEjb responseEjb = tq.getSingleResult();
            response = MapStruct.INSTANCE.userEjbToUser(responseEjb);
        } catch (NoResultException nre) {
            logger.info("Not found User with email: |{}|", email);
        }
        return response;
    }
}
