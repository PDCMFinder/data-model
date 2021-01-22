package org.pdxfinder.graph.dao;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Represent the PDX model
 * The model will have at least one PdxPassage to capture the model creation event
 */
@NodeEntity
public class ModelCreation {

    @Id
    @GeneratedValue
    private Long id;

    @Index
    private String sourcePdxId;
    @Index
    private String dataSource;

    private Boolean omicDataShareable;
    private List<String> omicRawDataFile;


    @Relationship(type = "QUALITY_ASSURED_BY")
    private List<QualityAssurance> qualityAssurance;

    @Relationship(type = "IMPLANTED_IN", direction = Relationship.INCOMING)
    private Sample sample;

    @Relationship(type= "MODEL_SAMPLE_RELATION", direction = Relationship.INCOMING)
    private Set<Sample> relatedSamples;

    @Relationship(type = "SPECIMENS", direction = Relationship.INCOMING)
    private Set<Specimen> specimens;

    @Relationship(type = "SUMMARY_OF_TREATMENT", direction = Relationship.INCOMING)
    private TreatmentSummary treatmentSummary;

    @Relationship(type = "EXTERNAL_URL", direction = Relationship.INCOMING)
    private List<ExternalUrl> externalUrls;

    @Relationship(type = "GROUP", direction = Relationship.INCOMING)
    private Set<Group> groups;

    //support constructor with list of QA
    public ModelCreation(String sourcePdxId, String dataSource, Sample sample, List<QualityAssurance> qualityAssurance,List<ExternalUrl> externalUrls) {
        this.sourcePdxId = sourcePdxId;
        this.dataSource = dataSource;
        this.sample = sample;
        this.qualityAssurance = qualityAssurance;
        this.externalUrls = externalUrls;
        this.omicDataShareable = false;
    }
    //constructor for single QA
    public ModelCreation(String sourcePdxId, String dataSource, Sample sample, QualityAssurance qualityAssurance,List<ExternalUrl> externalUrls) {
        this.sourcePdxId = sourcePdxId;
        this.dataSource = dataSource;
        this.sample = sample;

        this.qualityAssurance = new ArrayList<>();
        this.qualityAssurance.add(qualityAssurance);

        this.externalUrls = externalUrls;
        this.omicDataShareable = false;
    }


    public ModelCreation() {
        // Empty constructor required as of Neo4j API 2.0.5
        this.omicDataShareable = false;
    }

    public ModelCreation(String sourcePdxId) {
        this.omicDataShareable = false;
        this.sourcePdxId = sourcePdxId;
    }

    public Long getId() {
        return id;
    }

    public String getSourcePdxId() {
        return sourcePdxId;
    }

    public void setSourcePdxId(String sourcePdxId) {
        this.sourcePdxId = sourcePdxId;
    }

    public List<QualityAssurance> getQualityAssurance() {
        return qualityAssurance;
    }

    public void setQualityAssurance(List<QualityAssurance> qualityAssurance) {
        this.qualityAssurance = qualityAssurance;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Set<Sample> getRelatedSamples() {
        return relatedSamples;
    }

    public void setRelatedSamples(Set<Sample> relatedSamples) {
        this.relatedSamples = relatedSamples;
    }

    public void addRelatedSample(Sample sample){

        if(this.relatedSamples == null){
            this.relatedSamples = new HashSet<>();
        }

        this.relatedSamples.add(sample);
    }

    public Set<Specimen> getSpecimens() {
        return specimens;
    }

    public void setSpecimens(Set<Specimen> specimens) {
        this.specimens = specimens;
    }

    public boolean hasSpecimens() {
        return CollectionUtils.isNotEmpty(specimens);
    }

    public void addSpecimen(Specimen specimen){

        if(this.specimens == null){
            this.specimens = new HashSet<>();
        }

        this.specimens.add(specimen);
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public TreatmentSummary getTreatmentSummary() {
        return treatmentSummary;
    }

    public void setTreatmentSummary(TreatmentSummary treatmentSummary) {
        this.treatmentSummary = treatmentSummary;
    }

    public List<ExternalUrl> getExternalUrls() {
        return externalUrls;
    }

    public void setExternalUrls(List<ExternalUrl> externalUrls) {
        this.externalUrls = externalUrls;
    }

    public void addQualityAssurance(QualityAssurance qa){

        if(this.qualityAssurance == null ){
            this.qualityAssurance = new ArrayList<>();
        }

        this.qualityAssurance.add(qa);
    }


    public Boolean getOmicDataShareable() {
        return omicDataShareable;
    }

    public void setOmicDataShareable(Boolean omicDataShareable) {
        this.omicDataShareable = omicDataShareable;
    }

    public List<String> getOmicRawDataFile() {
        return omicRawDataFile;
    }

    public void setOmicRawDataFile(List<String> omicRawDataFile) {
        this.omicRawDataFile = omicRawDataFile;
    }

    public void addOmicRawDataFile(String file){

        if(omicRawDataFile == null){

            omicRawDataFile = new ArrayList<>();
        }

        omicRawDataFile.add(file);
        omicDataShareable = true;

    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public void addGroup(Group g){

        if(this.groups == null){

            this.groups = new HashSet<>();
        }

        this.groups.add(g);
    }

    public Specimen getSpecimenByPassageAndHostStrain(String passage, String hostStrain){

        if(specimens == null || specimens.size() == 0) return null;

        for(Specimen sp : specimens){

            if(sp != null && sp.getPassage() != null && sp.getPassage().equals(passage) && sp.getHostStrain() != null &&
                    sp.getHostStrain().getSymbol().equals(hostStrain)) return sp;

        }

        return null;
    }

    public void addTreatmentProtocol(TreatmentProtocol treatmentProtocol){

        if(treatmentSummary == null){

            treatmentSummary = new TreatmentSummary();
        }

        treatmentSummary.addTreatmentProtocol(treatmentProtocol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ModelCreation that = (ModelCreation) o;

        return new EqualsBuilder()
            .append(getSourcePdxId(), that.getSourcePdxId())
            .append(getDataSource(), that.getDataSource())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(getSourcePdxId())
            .append(getDataSource())
            .toHashCode();
    }

    @Override
    public String toString() {
        return String.format("[%s - %s]", this.sourcePdxId, this.dataSource);
    }
}
