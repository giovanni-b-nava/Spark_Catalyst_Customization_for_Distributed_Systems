package CostModel;

import AuthorizationModel.AuthorizationModel;
import ConfigurationParser.Node;
import RelationProfileTreeBuilder.Relation;
import RelationProfileTreeBuilder.RelationProfile;
import TreeStructure.BinaryNode;

/**
 * Created by Giovanni on 28/11/2016.
 */
public class CostModel
{

    private RelationProfile updateRelationProfile(Node providerTo, BinaryNode<Relation> relationNode)
    {
        RelationProfile fatherProfile = relationNode.getFather().getElement().getProfile();
        RelationProfile currentProfile = relationNode.getElement().getProfile();

        RelationProfile updatedProfile = new RelationProfile();

        // For all the father's visible plaintext attributes ...
        for (int i=0; i < fatherProfile.getVisiblePlaintext().size(); i++)
        {
            String currentAttribute = fatherProfile.getVisiblePlaintext().get(i);

            // If the current attribute visibility is Plaintext for provider to ...
            if (AuthorizationModel.checkVisibility(providerTo, currentAttribute,"Plaintext"))
            {
                // If current profile doesn't contain in the visible plaintext the current attribute...
                if (!currentProfile.getVisiblePlaintext().contains(currentAttribute))
                {
                    // If current profile contains in the visibile encrypted the current attribute
                    if (currentProfile.getVisibleEncrypted().contains(currentAttribute))
                    {
                        // Update the relation profile moving the attriitute from the visible plaintext
                        // to the visible encrypted
                        updatedProfile = fatherProfile;
                        updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                        updatedProfile.getVisibleEncrypted().add(currentAttribute);
                    }
                    else
                        System.out.println("CostModel.UpdateRelationProfile: ERROR invalid attribute (visibility#1)");
                }
            }
            // The current attribute visibility is Encrypted for provider to
            else
            {
                // If current profile contains in the visible plaintext the current attribute
                if (currentProfile.getVisiblePlaintext().contains(currentAttribute))
                {
                    // Update the relation profile moving the attribute from the visible plaintext
                    // to the visible encrypted
                    updatedProfile = fatherProfile;
                    updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                    updatedProfile.getVisibleEncrypted().add(currentAttribute);
                }
                else if (!currentProfile.getVisiblePlaintext().contains(currentAttribute))
                    System.out.println("CostModel.UpdateRelationProfile: ERROR invalid attribute (visibility#2)");
            }
        }

        // For all the father's visible encrypted attributes ...
        for (int i=0; i < fatherProfile.getVisibleEncrypted().size(); i++)
        {
            String currentAttribute = fatherProfile.getVisibleEncrypted().get(i);

            // If the current attribute visibility is Encrypted for provider to ...
            if (AuthorizationModel.checkVisibility(providerTo, currentAttribute,"Encrypted"))
            {
                // If current profile doesn't contain in the visible encrypted the current attribute...
                if (!currentProfile.getVisibleEncrypted().contains(currentAttribute))
                {
                    // If current profile contains in the visible plaintext the current attribute
                    if (currentProfile.getVisiblePlaintext().contains(currentAttribute))
                    {
                        // Update the relation profile moving the attribute from the visible encrypted
                        // to the visible plaintext
                        updatedProfile = fatherProfile;
                        updatedProfile.getVisibleEncrypted().remove(currentAttribute);
                        updatedProfile.getVisiblePlaintext().add(currentAttribute);
                    }
                    else
                        System.out.println("CostModel.UpdateRelationProfile: ERROR invalid attribute (visibility#3)");
                }
            }
            // The current attribute visibility is Plaintext for provider to
            else
            {
                // If current profile contains in the visible encrypted the current attribute
                if (currentProfile.getVisibleEncrypted().contains(currentAttribute))
                {
                    // Update the relation profile moving the attribute from the visible encrypted
                    // to the visible plaintext
                    updatedProfile = fatherProfile;
                    updatedProfile.getVisibleEncrypted().remove(currentAttribute);
                    updatedProfile.getVisiblePlaintext().add(currentAttribute);
                }
                else if (!currentProfile.getVisibleEncrypted().contains(currentAttribute))
                    System.out.println("CostModel.UpdateRelationProfile: ERROR invalid attribute (visibility#4)");
            }
        }

        return updatedProfile;
    }






}
