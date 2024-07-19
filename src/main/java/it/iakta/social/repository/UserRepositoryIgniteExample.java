//package it.iakta.social.repository;
//
//import java.util.List;
//
//import javax.cache.Cache;
//
//import org.apache.ignite.springdata.repository.IgniteRepository;
//import org.apache.ignite.springdata.repository.config.Query;
//import org.apache.ignite.springdata.repository.config.RepositoryConfig;
//import org.springframework.data.domain.Pageable;
//
//import it.iakta.social.entity.User;
//
//@RepositoryConfig(cacheName = "UserCache")
//public interface UserRepositoryIgniteExample extends IgniteRepository<User, Long> {
//    /**
//     * Gets all the persons with the given name.
//     * @param name Person name.
//     * @return A list of Persons with the given first name.
//     */
//    public List<User> findByFirstName(String name);
//
//    /**
//     * Returns top Person with the specified surname.
//     * @param name Person surname.
//     * @return Person that satisfy the query.
//     */
//    public Cache.Entry<Long, User> findTopByLastNameLike(String name);
//
//    /**
//     * Getting ids of all the Person satisfying the custom query from {@link Query} annotation.
//     *
//     * @param orgId Query parameter.
//     * @param pageable Pageable interface.
//     * @return A list of Persons' ids.
//     */
//    @Query("SELECT id FROM Person WHERE orgId > ?")
//    public List<Long> selectId(long orgId, Pageable pageable);
//}