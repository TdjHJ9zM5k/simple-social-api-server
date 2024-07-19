///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package it.iakta.social.examples;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.TreeMap;
//
//import javax.cache.Cache;
//
//import org.apache.ignite.Ignite;
//import org.apache.ignite.Ignition;
//import org.apache.ignite.configuration.CacheConfiguration;
//import org.apache.ignite.configuration.IgniteConfiguration;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.data.domain.PageRequest;
//
//import it.iakta.social.configuration.IgniteClientSpringApplicationConfiguration;
//import it.iakta.social.configuration.SpringApplicationConfiguration;
//import it.iakta.social.entity.User;
//import it.iakta.social.repository.UserRepositoryIgniteExample;
//
///**
// * The example demonstrates how to interact with an Apache Ignite cluster by means of Spring Data API.
// */
//public class SpringDataExample {
//    /** Spring Application Context. */
//    private static AnnotationConfigApplicationContext ctx;
//
//    /** Ignite Spring Data repository. */
//    private static UserRepositoryIgniteExample repo;
//
//    /**
//     * Execute examples involving both approaches to configure Spring Data repository access to an Ignite cluster:
//     *      through Ignite thin client and through Ignite node.
//     * @param args Command line arguments, none required.
//     */
//    public static void main(String[] args) {
//        try (Ignite ignored = startIgniteNode()) {
//            // Ignite node instance is used to configure access to the Ignite cluster.
//            doSpringDataExample(SpringApplicationConfiguration.class);
//
//            // Ignite thin client instance is used to configure access to the Ignite cluster.
//            doSpringDataExample(IgniteClientSpringApplicationConfiguration.class);
//        }
//    }
//
//    /** Starts an Ignite node that simulates an Ignite cluster to which Spring Data repository will perform access. */
//    private static Ignite startIgniteNode() {
//        IgniteConfiguration cfg = new IgniteConfiguration()
//            .setPeerClassLoadingEnabled(true)
//            .setCacheConfiguration(new CacheConfiguration<Long, User>("UserCache")
//                .setIndexedTypes(Long.class, User.class));
//
//        return Ignition.start(cfg);
//    }
//
//    /**
//     * Performs basic Spring Data repository operation.
//     *
//     * @param springAppCfg Class of Spring application configuration that will be used for Spring context initialization.
//     */
//    private static void doSpringDataExample(Class<?> springAppCfg) {
//        igniteSpringDataInit(springAppCfg);
//
//        populateRepository();
//
//        findUsers();
//
//        queryRepository();
//
//        System.out.println("\n>>> Cleaning out the repository...");
//
//        repo.deleteAll();
//
//        System.out.println("\n>>> Repository size: " + repo.count());
//
//        // Destroying the context.
//        ctx.close();
//    }
//
//    /**
//     * Initializes Spring Data and Ignite repositories.
//     *
//     * @param springAppCfg Class of Spring application configuration that will be used for Spring context initialization.
//     */
//    private static void igniteSpringDataInit(Class<?> springAppCfg) {
//        ctx = new AnnotationConfigApplicationContext();
//
//        // Explicitly registering Spring configuration.
//        ctx.register(springAppCfg);
//
//        ctx.refresh();
//
//        // Getting a reference to UserRepository.
//        repo = ctx.getBean(UserRepositoryIgniteExample.class);
//    }
//
//    /**
//     * Fills the repository in with sample data.
//     */
//    private static void populateRepository() {
//        TreeMap<Long, User> Users = new TreeMap<>();
//
//        Users.put(1L, new User("user1", "password1"));
//        Users.put(2L, new User("user2", "password2"));
//
//        // Adding data into the repository.
//        repo.save(Users);
//
//        System.out.println("\n>>> Added " + repo.count() + " Users into the repository.");
//    }
//
//    /**
//     * Gets a list of Users using standard read operations.
//     */
//    private static void findUsers() {
//        // Getting User with specific ID.
//        User User = repo.findById(2L).orElse(null);
//
//        System.out.println("\n>>> Found User [id=" + 2L + ", val=" + User + "]");
//
//        // Getting a list of Users.
//
//        ArrayList<Long> ids = new ArrayList<>();
//
//        for (long i = 0; i < 5; i++)
//            ids.add(i);
//
//        Iterator<User> Users = repo.findAllById(ids).iterator();
//
//        System.out.println("\n>>> Users list for specific ids: ");
//
//        while (Users.hasNext())
//            System.out.println("   >>>   " + Users.next());
//    }
//
//    /**
//     * Execute advanced queries over the repository.
//     */
//    private static void queryRepository() {
//        System.out.println("\n>>> Users with name 'John':");
//
//        List<User> Users = repo.findByFirstName("John");
//
//        for (User User : Users)
//            System.out.println("   >>>   " + User);
//
//        Cache.Entry<Long, User> topUser = repo.findTopByLastNameLike("Smith");
//
//        System.out.println("\n>>> Top User with surname 'Smith': " + topUser.getValue());
//
//        List<Long> ids = repo.selectId(1000L, PageRequest.of(0, 4));
//
//        System.out.println("\n>>> Users working for organization with ID > 1000: ");
//
//        for (Long id: ids)
//            System.out.println("   >>>   [id=" + id + "]");
//    }
//}
