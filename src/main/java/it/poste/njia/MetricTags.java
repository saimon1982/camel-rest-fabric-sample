package it.poste.njia;

import java.util.List;
import java.util.ArrayList;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Tag;

/**
 * Class MetricTags. Configuration property for tags.
 *
 */
@Component
@ConfigurationProperties("metrics")
public class MetricTags{

  
  /** 
   * The {@link List} of tags
   */
  private List<MetricTag> tags = new ArrayList<>();

  /**
   * Inner static class for the single custom tag configuration. 
   */
  public static class MetricTag{
  
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
   * @return a List of MetricTag
   */
  public List<MetricTag> getTags(){
    return this.tags;
  }
  /**
   * Sets the List of Tags
   * @param tags a List of MetricTag
   */
  public void setTags(List<MetricTag> tags){
    this.tags = tags;
  }

  /**
   * Returns the list of tags as a List of micrometer Tag objects
   * @return a List of Tag objects.
   */
  public List<Tag> getMicrometerTags(){
    List<Tag> mTags = new ArrayList<>();
    if(null != tags){
      for(MetricTag tag: getTags()){
        mTags.add(Tag.of(tag.getName(),tag.getValue()));
      }
    }
    return mTags;
  }

}
