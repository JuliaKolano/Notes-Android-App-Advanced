package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasOne;
import com.amplifyframework.core.model.annotations.HasMany;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.ModelIdentifier;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the User type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Users", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class User implements Model {
  public static final QueryField ID = field("User", "id");
  public static final QueryField EMAIL = field("User", "email");
  public static final QueryField NAME = field("User", "name");
  public static final QueryField PROFILE_PICTURE = field("User", "profilePicture");
  public static final QueryField USER_NOTES_ID = field("User", "userNotesId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String email;
  private final @ModelField(targetType="String") String name;
  private final @ModelField(targetType="String") String profilePicture;
  private final @ModelField(targetType="Notes") @HasOne(associatedWith = "id", type = Notes.class) Notes Notes = null;
  private final @ModelField(targetType="UserSharedTask") @HasMany(associatedWith = "user", type = UserSharedTask.class) List<UserSharedTask> SharedTasks = null;
  private final @ModelField(targetType="Task") @HasMany(associatedWith = "userID", type = Task.class) List<Task> Tasks = null;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  private final @ModelField(targetType="ID") String userNotesId;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getEmail() {
      return email;
  }
  
  public String getName() {
      return name;
  }
  
  public String getProfilePicture() {
      return profilePicture;
  }
  
  public Notes getNotes() {
      return Notes;
  }
  
  public List<UserSharedTask> getSharedTasks() {
      return SharedTasks;
  }
  
  public List<Task> getTasks() {
      return Tasks;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public String getUserNotesId() {
      return userNotesId;
  }
  
  private User(String id, String email, String name, String profilePicture, String userNotesId) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.profilePicture = profilePicture;
    this.userNotesId = userNotesId;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      User user = (User) obj;
      return ObjectsCompat.equals(getId(), user.getId()) &&
              ObjectsCompat.equals(getEmail(), user.getEmail()) &&
              ObjectsCompat.equals(getName(), user.getName()) &&
              ObjectsCompat.equals(getProfilePicture(), user.getProfilePicture()) &&
              ObjectsCompat.equals(getCreatedAt(), user.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), user.getUpdatedAt()) &&
              ObjectsCompat.equals(getUserNotesId(), user.getUserNotesId());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getEmail())
      .append(getName())
      .append(getProfilePicture())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getUserNotesId())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("User {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("email=" + String.valueOf(getEmail()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("profilePicture=" + String.valueOf(getProfilePicture()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("userNotesId=" + String.valueOf(getUserNotesId()))
      .append("}")
      .toString();
  }
  
  public static EmailStep builder() {
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
  public static User justId(String id) {
    return new User(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      email,
      name,
      profilePicture,
      userNotesId);
  }
  public interface EmailStep {
    BuildStep email(String email);
  }
  

  public interface BuildStep {
    User build();
    BuildStep id(String id);
    BuildStep name(String name);
    BuildStep profilePicture(String profilePicture);
    BuildStep userNotesId(String userNotesId);
  }
  

  public static class Builder implements EmailStep, BuildStep {
    private String id;
    private String email;
    private String name;
    private String profilePicture;
    private String userNotesId;
    public Builder() {
      
    }
    
    private Builder(String id, String email, String name, String profilePicture, String userNotesId) {
      this.id = id;
      this.email = email;
      this.name = name;
      this.profilePicture = profilePicture;
      this.userNotesId = userNotesId;
    }
    
    @Override
     public User build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new User(
          id,
          email,
          name,
          profilePicture,
          userNotesId);
    }
    
    @Override
     public BuildStep email(String email) {
        Objects.requireNonNull(email);
        this.email = email;
        return this;
    }
    
    @Override
     public BuildStep name(String name) {
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep profilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
        return this;
    }
    
    @Override
     public BuildStep userNotesId(String userNotesId) {
        this.userNotesId = userNotesId;
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
    private CopyOfBuilder(String id, String email, String name, String profilePicture, String userNotesId) {
      super(id, email, name, profilePicture, userNotesId);
      Objects.requireNonNull(email);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder profilePicture(String profilePicture) {
      return (CopyOfBuilder) super.profilePicture(profilePicture);
    }
    
    @Override
     public CopyOfBuilder userNotesId(String userNotesId) {
      return (CopyOfBuilder) super.userNotesId(userNotesId);
    }
  }
  

  public static class UserIdentifier extends ModelIdentifier<User> {
    private static final long serialVersionUID = 1L;
    public UserIdentifier(String id) {
      super(id);
    }
  }
  
}
