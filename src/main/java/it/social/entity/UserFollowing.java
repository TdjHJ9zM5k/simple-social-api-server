package it.social.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_follows")
public class UserFollowing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_follow_id")
    private Long userFollowId;

    public UserFollowing() {}
    
	public UserFollowing(Long userId) {
		this.userId = userId;
	}

    public UserFollowing(Long userId, Long userFollowId) {
        this.userId = userId;
        this.userFollowId = userFollowId;
    }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserFollowId() {
		return userFollowId;
	}

	public void setUserFollowId(Long userFollowId) {
		this.userFollowId = userFollowId;
	}

	public Long getId() {
		return id;
	}

}