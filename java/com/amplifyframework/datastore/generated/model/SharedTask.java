package com.amplifyframework.datastore.generated.model;

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

/** This is an auto generated class representing the SharedTask type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "SharedTasks", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class SharedTask implements Model {
  public static final QueryField ID = field("SharedTask", "id");
  public static final QueryField NAME = field("SharedTask", "name");
  public static final QueryField DESCRIPTION = field("SharedTask", "description");
  public static final QueryField TIME = field("SharedTask", "time");
  public static final QueryField PRIORITY = field("SharedTask", "priority");
  public static final QueryField COMPLETED = field("SharedTask", "completed");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String name;
  private final @ModelField(targetType="String") String description;
  private final @ModelField(targetType="String") String time;
  private final @ModelField(targetType="String") String priority;
  private final @ModelField(targetType="String") String completed;
  private final @ModelField(targetType="UserSharedTask") @HasMany(associatedWith = "sharedTask", type = UserSharedTask.class) List<UserSharedTask> users = null;
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
  
  public String getName() {
      return name;
  }
  
  public String getDescription() {
      return description;
  }
  
  public String getTime() {
      return time;
  }
  
  public String getPriority() {
      return priority;
  }
  
  public String getCompleted() {
      return completed;
  }
  
  public List<UserSharedTask> getUsers() {
      return users;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private SharedTask(String id, String name, String description, String time, String priority, String completed) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.time = time;
    this.priority = priority;
    this.completed = completed;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      SharedTask sharedTask = (SharedTask) obj;
      return ObjectsCompat.equals(getId(), sharedTask.getId()) &&
              ObjectsCompat.equals(getName(), sharedTask.getName()) &&
              ObjectsCompat.equals(getDescription(), sharedTask.getDescription()) &&
              ObjectsCompat.equals(getTime(), sharedTask.getTime()) &&
              ObjectsCompat.equals(getPriority(), sharedTask.getPriority()) &&
              ObjectsCompat.equals(getCompleted(), sharedTask.getCompleted()) &&
              ObjectsCompat.equals(getCreatedAt(), sharedTask.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), sharedTask.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getDescription())
      .append(getTime())
      .append(getPriority())
      .append(getCompleted())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("SharedTask {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("time=" + String.valueOf(getTime()) + ", ")
      .append("priority=" + String.valueOf(getPriority()) + ", ")
      .append("completed=" + String.valueOf(getCompleted()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
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
  public static SharedTask justId(String id) {
    return new SharedTask(
      id,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      description,
      time,
      priority,
      completed);
  }
  public interface BuildStep {
    SharedTask build();
    BuildStep id(String id);
    BuildStep name(String name);
    BuildStep description(String description);
    BuildStep time(String time);
    BuildStep priority(String priority);
    BuildStep completed(String completed);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String name;
    private String description;
    private String time;
    private String priority;
    private String completed;
    public Builder() {
      
    }
    
    private Builder(String id, String name, String description, String time, String priority, String completed) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.time = time;
      this.priority = priority;
      this.completed = completed;
    }
    
    @Override
     public SharedTask build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new SharedTask(
          id,
          name,
          description,
          time,
          priority,
          completed);
    }
    
    @Override
     public BuildStep name(String name) {
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep description(String description) {
        this.description = description;
        return this;
    }
    
    @Override
     public BuildStep time(String time) {
        this.time = time;
        return this;
    }
    
    @Override
     public BuildStep priority(String priority) {
        this.priority = priority;
        return this;
    }
    
    @Override
     public BuildStep completed(String completed) {
        this.completed = completed;
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
    private CopyOfBuilder(String id, String name, String description, String time, String priority, String completed) {
      super(id, name, description, time, priority, completed);
      
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder time(String time) {
      return (CopyOfBuilder) super.time(time);
    }
    
    @Override
     public CopyOfBuilder priority(String priority) {
      return (CopyOfBuilder) super.priority(priority);
    }
    
    @Override
     public CopyOfBuilder completed(String completed) {
      return (CopyOfBuilder) super.completed(completed);
    }
  }
  

  public static class SharedTaskIdentifier extends ModelIdentifier<SharedTask> {
    private static final long serialVersionUID = 1L;
    public SharedTaskIdentifier(String id) {
      super(id);
    }
  }
  
}
