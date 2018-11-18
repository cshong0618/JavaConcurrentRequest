package org.shaong.javajsonplaceholder.services;

import lombok.extern.slf4j.Slf4j;
import org.shaong.javajsonplaceholder.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Slf4j
@Service
public class UserService {
    private final String baseUrl = "https://jsonplaceholder.typicode.com/";
    private ExecutorService executorService;

    @Autowired
    public UserService() {
        executorService = Executors.newScheduledThreadPool(1);
        ((ScheduledExecutorService) executorService).scheduleAtFixedRate(System::gc, 10, 10, TimeUnit.SECONDS);
    }

    public Optional<UserData> getUserData(Integer userId) throws InterruptedException, ExecutionException {
        ((ThreadPoolExecutor) executorService).setCorePoolSize(((ThreadPoolExecutor) executorService).getMaximumPoolSize());
        UserData userData = new UserData();

        CompletableFuture<User> userCompletableFuture = CompletableFuture.supplyAsync(getUser(userId), executorService)
                .exceptionally((e) -> new User());
        CompletableFuture<List<Todo>> todosCompletableFuture = CompletableFuture.supplyAsync(getTodos(userId), executorService)
                .exceptionally((e) -> new ArrayList<>());
        CompletableFuture<List<Post>> postsCompletableFuture = CompletableFuture.supplyAsync(getPosts(userId), executorService)
                .exceptionally((e) -> new ArrayList<>());

        CompletableFuture.allOf(userCompletableFuture, todosCompletableFuture, postsCompletableFuture).join();

        Optional.ofNullable(userCompletableFuture.get())
                .ifPresent(userData::setUser);
        Optional.ofNullable(todosCompletableFuture.get())
                .ifPresent(userData::setTodos);
        Optional.ofNullable(postsCompletableFuture.get())
                .ifPresent(userData::setPosts);

        ((ThreadPoolExecutor) executorService).setCorePoolSize(1);
        return Optional.of(userData);
    }

    private Supplier<User> getUser(Integer userId) {
        return () -> {
            String url = baseUrl + "users/" + userId;

            RestTemplate restTemplate = new RestTemplate();

            User user = restTemplate.getForObject(url, User.class);
            log.info(user.toString());
            return user;
        };
    }

    private Supplier<List<Todo>> getTodos(Integer userId) {
        return () -> {
            String url = baseUrl + "todos/?userId=" + userId;

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<List<Todo>> response =
                    restTemplate.exchange(url, HttpMethod.GET, null,
                            new ParameterizedTypeReference<List<Todo>>() {
                            });

            return response.getBody();
        };
    }

    private Supplier<List<Post>> getPosts(Integer userId) {
        return () -> {
            String url = baseUrl + "posts/?userId=" + userId;

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<List<Post>> response =
                    restTemplate.exchange(url, HttpMethod.GET, null,
                            new ParameterizedTypeReference<List<Post>>() {
                            });

            return response.getBody();
        };
    }

    @PreDestroy
    public void destroy() {
        try {
            executorService.shutdownNow();

            while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                log.info("Shutting down");
            }

            if(executorService.isTerminated()) {
                log.info("Shut down");
            }
        } catch (InterruptedException ex) {
            log.error("Shutdown failed");
        }
    }
}
