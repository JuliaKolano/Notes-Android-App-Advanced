package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.ModelIdentifier;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the UserSharedTask type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "UserSharedTasks", type = Model.Type.USER, version = 1)
@Index(name = "bySharedTask", fields = {"sharedTaskId"})
@Index(name = "byUser", fields = {"userId"})
public final class UserSharedTask implements Model {
  public static final QueryField ID = field("UserSharedTask", "id");
  public static final QueryField SHARED_TASK = field("UserSharedTask", "sharedTaskId");
  public static final QueryField USER = field("UserSharedTask", "userId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="SharedTask", isRequired = true) @BelongsTo(targetName = "sharedTaskId", targetNames = {"sharedTaskId"}, type = SharedTask.class) SharedTask sharedTask;
  private final @ModelField(targetType="User", isRequired = true) @BelongsTo(targetName = "userId", targetNames = {"userId"}, type = User.class) User user;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public SharedTask getSharedTask() {
      return sharedTask;
  }
  
  public User getUser() {
      return user;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private UserSharedTask(String id, SharedTask sharedTask, User user) {
    this.id = id;
    this.sharedTask = sharedTask;
    this.user = user;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      UserSharedTask userSharedTask = (UserSharedTask) obj;
      return ObjectsCompat.equals(getId(), userSharedTask.getId()) &&
              ObjectsCompat.equals(getSharedTask(), userSharedTask.getSharedTask()) &&
              ObjectsCompat.equals(getUser(), userSharedTask.getUser()) &&
              ObjectsCompat.equals(getCreatedAt(), userSharedTask.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), userSharedTask.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getSharedTask())
      .append(getUser())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("UserSharedTask {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("sharedTask=" + String.valueOf(getSharedTask()) + ", ")
      .append("user=" + String.valueOf(getUser()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static SharedTaskStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static UserSharedTask justId(String id) {
    return new UserSharedTask(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      sharedTask,
      user);
  }
  public interface SharedTaskStep {
    UserStep sharedTask(SharedTask sharedTask);
  }
  

  public interface UserStep {
    BuildStep user(User user);
  }
  

  public interface BuildStep {
    UserSharedTask build();
    BuildStep id(String id);
  }
  

  public static class Builder implements SharedTaskStep, UserStep, BuildStep {
    private String id;
    private SharedTask sharedTask;
    private User user;
    public Builder() {
      
    }
    
    private Builder(String id, SharedTask sharedTask, User user) {
      this.id = id;
      this.sharedTask = sharedTask;
      this.user = user;
    }
    
    @Override
     public UserSharedTask build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new UserSharedTask(
          id,
          sharedTask,
          user);
    }
    
    @Override
     public UserStep sharedTask(SharedTask sharedTask) {
        Objects.requireNonNull(sharedTask);
        this.sharedTask = sharedTask;
        return this;
    }
    
    @Override
     public BuildStep user(User user) {
        Objects.requireNonNull(user);
        this.user = user;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, SharedTask sharedTask, User user) {
      super(id, sharedTask, user);
      Objects.requireNonNull(sharedTask);
      Objects.requireNonNull(user);
    }
    
    @Override
     public CopyOfBuilder sharedTask(SharedTask sharedTask) {
      return (CopyOfBuilder) super.sharedTask(sharedTask);
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
  }
  

  public static class UserSharedTaskIdentifier extends ModelIdentifier<UserSharedTask> {
    private static final long serialVersionUID = 1L;
    public UserSharedTaskIdentifier(String id) {
      super(id);
    }
  }
  
}
