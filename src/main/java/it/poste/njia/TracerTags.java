package it.poste.njia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class TracerTags. Configuration property for tags.
 *
 */
@Component
@ConfigurationProperties("tracer")
public class TracerTags{

  
  /** 
   * The {@link List} of tags
   */
  private List<TracerTag> tags = new ArrayList<>();

  /**
   * Inner static class for the single custom tag configuration. 
   */
  public static class TracerTag{
  
     private String name;
     private String value;
   
   
     /**
      * Returns the name of the tag.
      * @return the name as a {@link String}
      */
     public String getName(){
       return this.name;
     }
     /**
      * Sets the name of the tag.
      * @param name the name as a {@link String}
      */
     public void setName(String name){
       this.name = name;
     }
     /**
      * Gets the value of the tag.
      * @return the value as a {@link String}
      */
     public String getValue(){
       return this.value;
     }
     /**
      * Sets the value of the tag.
      * @param value the value as a {@link String}
      */
     public void setValue(String value){
       this.value = value;
     }
  }


  /**
   * Gets the List of Tags
   * @return a List of TracerTag
   */
  public List<TracerTag> getTags(){
    return this.tags;
  }
  /**
   * Sets the List of Tags
   * @param tags a List of TracerTag
   */
  public void setTags(List<TracerTag> tags){
    this.tags = tags;
  }

  /**
   * Returns the list of tags as a List of micrometer Tag objects
   * @return a List of Tag objects.
   */
  public Map<String,String> getTracerTags(){
    Map<String,String> mTags = new HashMap<>();
    if(null != tags){
      for(TracerTag tag: getTags()){
        mTags.put(tag.getName(), tag.getValue());
      }
    }
    return mTags;
  }

}
