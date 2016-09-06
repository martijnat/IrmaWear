package com.example.terp.abcwear;


public class AbcProofGenerator {

    // Example variable
    private Boolean IsInitialized = false;
    private String mProof = "DUMMY DATA";

    // Requires variables used to generate proof
    // Is dummy code for now
    public void AbcProofGenerator (){
        // Do Something
        IsInitialized = true;
    }

    public void setProof(String x){
        mProof=x;
    }
    public String getProof(){
        return mProof;
    }

    // Dummy code
    public Boolean Initialized (){
        return IsInitialized;
    }

    //Dummy code, proof is silly
    public String GenerateProof ( String Attribute ){
        String Proof = "Proof of [" + Attribute +"]";
        return Proof;
    }
}
