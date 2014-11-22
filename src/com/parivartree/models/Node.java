package com.parivartree.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

public class Node implements Serializable {

	/**
	 * This version is synchronized with the JSON for the TreeView of the
	 * production server
	 */
	private static final long serialVersionUID = 7766580249570218877L;
	JSONObject data;
	final String TAG = "Node";
	
	int id, loginStatus, invited, gender, deceased, relationCount;
	int relationId = 0;
	
	String name, firstName, lastName, city, image;
	
	Context context;
	
	Bitmap userImage;
	
	ArrayList<Node> fathers = new ArrayList<Node>();
	ArrayList<Node> mothers = new ArrayList<Node>();
	ArrayList<Node> wives = new ArrayList<Node>();
	ArrayList<Node> husbands = new ArrayList<Node>();
	ArrayList<Node> brothers = new ArrayList<Node>();
	ArrayList<Node> sisters = new ArrayList<Node>();
	ArrayList<Node> sons = new ArrayList<Node>();
	ArrayList<Node> daughters = new ArrayList<Node>();

	public Node() {
	}

	public Node(JSONObject data, Context context) {
		this.data = data;
		initializeNode();
		this.context = context;
	}

	private void initializeNode() {
		Log.d(TAG, "initialize Node called");
		if (data.has("top")) {
			Log.d(TAG, "in top");
			try {
				JSONArray parallelData = data.getJSONArray("top");
				if (parallelData.length() > 0) {
					JSONObject item0 = (JSONObject) parallelData.get(0);
					JSONArray fatherParams = item0.getJSONArray("params");
					JSONObject fatherTempObject = new JSONObject();
					for (int i = 0; i < fatherParams.length(); i++) {
						
						fatherTempObject = (JSONObject) fatherParams.get(i);
						if (fatherTempObject.getInt("relationid") == 1) {
							fathers.add(setNode(fatherTempObject,1));
							Log.d(TAG, "in father");
							Log.d(TAG, "father id: " + fatherTempObject.getInt("id"));
						}
						/*
						 * else if(fatherTempObject.getInt("relationid") == 2) {
						 * Log.d(TAG, "in mother"); Log.d(TAG, "mother id: " +
						 * fatherTempObject.getInt("id"));
						 * mothers.add(setNode(fatherTempObject)); }
						 */
					}
				}
				
				if (parallelData.length() > 1) {
					JSONObject item1 = (JSONObject) parallelData.get(1);
					JSONArray motherParams = item1.getJSONArray("params");
					JSONObject motherTempObject = new JSONObject();
					for (int i = 0; i < motherParams.length(); i++) {
						
						motherTempObject = (JSONObject) motherParams.get(i);
						if (motherTempObject.getInt("relationid") == 2) {
							Log.d(TAG, "in mother");
							Log.d(TAG, "mother id: " + motherTempObject.getInt("id"));
							mothers.add(setNode(motherTempObject, 2));
						}
					}
				}
				
			} catch (JSONException e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: Treeview Fetch Parallel", "" + tempStack.getLineNumber() + " methodName: "
							+ tempStack.getClassName() + "-" + tempStack.getMethodName());
				}
				Log.d("Parsing error - ", e.getMessage());
				Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		// TODO parse parallel users
		if (data.has("parallel")) {
			try {
				JSONArray parallelData = data.getJSONArray("parallel");

				if (parallelData.length() > 0) {
					JSONObject userObject = (JSONObject) parallelData.get(0);
					if (userObject.getInt("relationid") == 0) {
						Log.v(TAG, "Parivar Tree user added");
						Log.d(TAG, "in user");
						Log.d(TAG, "user id: " + userObject.getInt("id"));
						this.id = (userObject.has("id")) ? userObject.getInt("id") : 0;
						this.relationId = (userObject.has("relationid")) ? userObject.getInt("relationid") : 0;
						this.firstName = (userObject.has("firstname")) ? userObject.getString("firstname")
								: "PRASAD-HC";
						this.lastName = (userObject.has("lastname")) ? userObject.getString("lastname") : "KATANKOT-HC";
						this.city = (userObject.has("city") && (userObject.get("city") instanceof String)) ? userObject
								.getString("city") : "" + userObject.getInt("city");
						this.loginStatus = (userObject.has("login_status")) ? userObject.getInt("login_status") : 0;
						this.invited = (userObject.has("invited")) ? userObject.getInt("invited") : 0;
						this.gender = (userObject.has("gender")) ? userObject.getInt("gender") : 0;
						this.deceased = (userObject.has("deceased")) ? userObject.getInt("deceased") : 0;
						this.name = this.firstName + " " + this.lastName;
					}
				}

				if (parallelData.length() > 1) {
					JSONObject item1 = (JSONObject) parallelData.get(1);
					JSONArray params = item1.getJSONArray("params");
					JSONObject tempObject = new JSONObject();
					for (int i = 0; i < params.length(); i++) {
						// Log.d("Parsing JSON", "Object number in parallel - "
						// + i + ", length:" + parallelData.length());
						tempObject = (JSONObject) params.get(i);
						if (tempObject.getInt("relationid") == 3) {
							Log.d(TAG, "in wife");
							Log.d(TAG, "wife id: " + tempObject.getInt("id"));
							wives.add(setNode(tempObject, 3));
						} else if (tempObject.getInt("relationid") == 8) {
							Log.d(TAG, "in husband");
							Log.d(TAG, "husband id: " + tempObject.getInt("id"));
							husbands.add(setNode(tempObject, 8));
						}
					}
				}
			} catch (JSONException e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: Treeview Fetch Parallel", "" + tempStack.getLineNumber() + " methodName: "
							+ tempStack.getClassName() + "-" + tempStack.getMethodName());
				}
				Log.d("Parsing error - ", e.getMessage());
				Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		// TODO parse child
		if (data.has("child")) {
			try {
				JSONArray parallelData = data.getJSONArray("child");
				if (parallelData.length() > 0) {
					JSONObject item0 = (JSONObject) parallelData.get(0);
					JSONArray params = item0.getJSONArray("params");
					JSONObject tempObject = new JSONObject();
					for (int i = 0; i < params.length(); i++) {
						// Log.d("Parsing JSON", "Object number in parallel - "
						// + i + ", length:" + parallelData.length());
						tempObject = (JSONObject) params.get(i);
						if (tempObject.getInt("relationid") == 6) {
							Log.d(TAG, "in son");
							Log.d(TAG, "son id: " + tempObject.getInt("id"));
							sons.add(setNode(tempObject, 6));
						} else if (tempObject.getInt("relationid") == 7) {
							Log.d(TAG, "in daughter");
							Log.d(TAG, "daughter id: " + tempObject.getInt("id"));
							daughters.add(setNode(tempObject, 7));
						}
					}
				}
			} catch (JSONException e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: Treeview Fetch Parallel", "" + tempStack.getLineNumber() + " methodName: "
							+ tempStack.getClassName() + "-" + tempStack.getMethodName());
				}
				Log.d("Parsing error - ", e.getMessage());
				Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		// TODO parse siblings
		if (data.has("sibling")) {
			try {
				JSONArray parallelData = data.getJSONArray("sibling");
				if (parallelData.length() > 0) {
					JSONObject item0 = (JSONObject) parallelData.get(0);
					JSONArray params = item0.getJSONArray("params");
					JSONObject tempObject = new JSONObject();
					for (int i = 0; i < params.length(); i++) {
						// Log.d("Parsing JSON", "Object number in parallel - "
						// + i + ", length:" + parallelData.length());
						tempObject = (JSONObject) params.get(i);
						Log.d(TAG, "siblings item" + tempObject.toString());
						if (tempObject.getInt("relationid") == 4) {
							Log.d(TAG, "in brother");
							Log.d(TAG, "brother id: " + tempObject.getInt("id"));
							// brothers.add(setNode(tempObject));
							Node tempBrotherNode = setNode(tempObject, 4);
							if (tempObject.has("kid")) {
								JSONArray kids = tempObject.getJSONArray("kid");
								for (int j = 0; j < kids.length(); j++) {
									JSONObject tempKid = kids.getJSONObject(j);
									if (tempKid.getInt("gender") == 1) {
										tempBrotherNode.addSon(setNode(tempKid));
									} else {
										tempBrotherNode.addDaughter(setNode(tempKid));
									}
								}
							}
							if (tempObject.has("wife")) {
								JSONArray wives = tempObject.getJSONArray("wife");
								for (int j = 0; j < wives.length(); j++) {
									JSONObject tempWife = wives.getJSONObject(j);
									tempBrotherNode.addWife(setNode(tempWife));
								}
							}
							brothers.add(tempBrotherNode);
						} else if (tempObject.getInt("relationid") == 5) {
							Log.d(TAG, "in sister");
							Log.d(TAG, "sister id: " + tempObject.getInt("id"));
							// sisters.add(setNode(tempObject));
							Node tempSisterNode = setNode(tempObject, 5);
							if (tempObject.has("kid")) {
								JSONArray kids = tempObject.getJSONArray("kid");
								for (int j = 0; j < kids.length(); j++) {
									JSONObject tempKid = kids.getJSONObject(j);
									if (tempKid.getInt("gender") == 1) {
										tempSisterNode.addSon(setNode(tempKid));
									} else {
										tempSisterNode.addDaughter(setNode(tempKid));
									}
								}
							}
							// the below line should be "husband" but was changed due to the response from the server
							if (tempObject.has("wife")) {
								JSONArray husbands = tempObject.getJSONArray("wife");
								for (int j = 0; j < husbands.length(); j++) {
									JSONObject tempHusband = husbands.getJSONObject(j);
									tempSisterNode.addHusbands(setNode(tempHusband));
								}
							}
							sisters.add(tempSisterNode);
						}
					}
				}
			} catch (JSONException e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: Treeview Fetch Parallel", "" + tempStack.getLineNumber() + " methodName: "
							+ tempStack.getClassName() + "-" + tempStack.getMethodName());
				}
				Log.d("Parsing error - ", e.getMessage());
				Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

	}

	public int getRelationId() {
		return relationId;
	}

	public void setRelationId(int relationId) {
		this.relationId = relationId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
	}

	public int getInvited() {
		return invited;
	}

	public void setInvited(int invited) {
		this.invited = invited;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getDeceased() {
		return deceased;
	}

	public void setDeceased(int deceased) {
		this.deceased = deceased;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
		this.name = this.firstName + " " + this.lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
		this.name = this.firstName + " " + this.lastName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public ArrayList<Node> getFathers() {
		return fathers;
	}

	public void addFather(Node father) {
		this.fathers.add(father);
	}

	public void setFathers(ArrayList<Node> fathers) {
		this.fathers = fathers;
	}

	public ArrayList<Node> getMothers() {
		return mothers;
	}

	public void addMother(Node mother) {
		this.mothers.add(mother);
	}

	public void setMothers(ArrayList<Node> mothers) {
		this.mothers = mothers;
	}

	public ArrayList<Node> getWives() {
		return wives;
	}

	public void addWife(Node wife) {
		this.wives.add(wife);
	}

	public void setWives(ArrayList<Node> wives) {
		this.wives = wives;
	}

	public ArrayList<Node> getHusbands() {
		return husbands;
	}

	public void addHusbands(Node husband) {
		this.husbands.add(husband);
	}

	public void setHusbands(ArrayList<Node> husbands) {
		this.husbands = husbands;
	}

	public ArrayList<Node> getBrothers() {
		return brothers;
	}

	public void addBrother(Node brother) {
		this.brothers.add(brother);
	}

	public void setBrothers(ArrayList<Node> brothers) {
		this.brothers = brothers;
	}

	public ArrayList<Node> getSisters() {
		return sisters;
	}

	public void addSister(Node sister) {
		this.sisters.add(sister);
	}

	public void setSisters(ArrayList<Node> sisters) {
		this.sisters = sisters;
	}

	public ArrayList<Node> getSons() {
		return sons;
	}

	public void addSon(Node son) {
		this.sons.add(son);
	}

	public void setSons(ArrayList<Node> sons) {
		this.sons = sons;
	}

	public ArrayList<Node> getDaughters() {
		return daughters;
	}

	public void addDaughter(Node daughter) {
		this.daughters.add(daughter);
	}

	public void setDaughters(ArrayList<Node> daughters) {
		this.daughters = daughters;
	}

	public int getRelationCount() {
		return relationCount;
	}

	public void setRelationCount(int relationCount) {
		if(relationCount < 0) {
			   this.relationCount = 0;
			  }
			  else {this.relationCount = relationCount;}
	}

	private Node setNode(JSONObject Node) {
		Node tempNode = new Node();
		try {
			tempNode.id = Node.getInt("id");
			tempNode.setFirstName(Node.getString("firstname"));
			tempNode.setLastName(Node.getString("lastname"));
			tempNode.setImage("http://www.parivartree.com/profileimages/thumbs/" + tempNode.getId() + "PROFILE.jpeg");
			tempNode.setGender(Integer.parseInt(Node.getString("gender")));
			tempNode.setDeceased(Integer.parseInt(Node.getString("deceased")));
			// tempNode.setRelationId(Integer.parseInt(Node.getString("relation")));
			tempNode.setRelationCount(Integer.parseInt(Node.getString("relationcount")));
			tempNode.setRelationId(0);
			// tempNode.setName(firstName); = this.firstName + " " +
			// this.lastName;
			// UrlImageViewHelper.setUrlDrawable(tempNode.userImage,
			// "http://example.com/image.png", null, 60000);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		return tempNode;
	}
	
	private Node setNode(JSONObject Node, int relationId) {
		Node tempNode = new Node();
		try {
			tempNode.id = Node.getInt("id");
			tempNode.setFirstName(Node.getString("firstname"));
			tempNode.setLastName(Node.getString("lastname"));
			tempNode.setImage("http://www.parivartree.com/profileimages/thumbs/" + tempNode.getId() + "PROFILE.jpeg");
			tempNode.setGender(Integer.parseInt(Node.getString("gender")));
			tempNode.setDeceased(Integer.parseInt(Node.getString("deceased")));
			// tempNode.setRelationId(Integer.parseInt(Node.getString("relation")));
			tempNode.setRelationCount(Integer.parseInt(Node.getString("relationcount")));
			tempNode.setRelationId(relationId);
			// tempNode.setName(firstName); = this.firstName + " " +
			// this.lastName;
			// UrlImageViewHelper.setUrlDrawable(tempNode.userImage,
			// "http://example.com/image.png", null, 60000);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		return tempNode;
	}

}
