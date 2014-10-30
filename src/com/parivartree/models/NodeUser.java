package com.parivartree.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

public class NodeUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -771670310404020449L;
	JSONObject data;
	final String TAG = "NodeUser";

	int relationId, id, loginStatus, invited, gender, deceased, relationCount;

	String name, firstName, lastName, city, image;

	Context context;

	Bitmap userImage;

	ArrayList<NodeUser> fathers = new ArrayList<NodeUser>();
	ArrayList<NodeUser> mothers = new ArrayList<NodeUser>();
	ArrayList<NodeUser> wives = new ArrayList<NodeUser>();
	ArrayList<NodeUser> husbands = new ArrayList<NodeUser>();
	ArrayList<NodeUser> brothers = new ArrayList<NodeUser>();
	ArrayList<NodeUser> sisters = new ArrayList<NodeUser>();
	ArrayList<NodeUser> sons = new ArrayList<NodeUser>();
	ArrayList<NodeUser> daughters = new ArrayList<NodeUser>();

	public NodeUser() {
	}

	public NodeUser(JSONObject data, Context context) {
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
							fathers.add(setNodeUser(fatherTempObject));
							Log.d(TAG, "in father");
							Log.d(TAG, "father id: " + fatherTempObject.getInt("id"));
						}
						/*
						 * else if(fatherTempObject.getInt("relationid") == 2) {
						 * Log.d(TAG, "in mother"); Log.d(TAG, "mother id: " +
						 * fatherTempObject.getInt("id"));
						 * mothers.add(setNodeUser(fatherTempObject)); }
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
							mothers.add(setNodeUser(motherTempObject));
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
							wives.add(setNodeUser(tempObject));
						} else if (tempObject.getInt("relationid") == 8) {
							Log.d(TAG, "in husband");
							Log.d(TAG, "husband id: " + tempObject.getInt("id"));
							husbands.add(setNodeUser(tempObject));
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
							sons.add(setNodeUser(tempObject));
						} else if (tempObject.getInt("relationid") == 7) {
							Log.d(TAG, "in daughter");
							Log.d(TAG, "daughter id: " + tempObject.getInt("id"));
							daughters.add(setNodeUser(tempObject));
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
							brothers.add(setNodeUser(tempObject));
						} else if (tempObject.getInt("relationid") == 5) {
							Log.d(TAG, "in sister");
							Log.d(TAG, "sister id: " + tempObject.getInt("id"));
							sisters.add(setNodeUser(tempObject));
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

	public ArrayList<NodeUser> getFathers() {
		return fathers;
	}

	public void addFather(NodeUser father) {
		this.fathers.add(father);
	}

	public void setFathers(ArrayList<NodeUser> fathers) {
		this.fathers = fathers;
	}

	public ArrayList<NodeUser> getMothers() {
		return mothers;
	}

	public void addMother(NodeUser mother) {
		this.mothers.add(mother);
	}

	public void setMothers(ArrayList<NodeUser> mothers) {
		this.mothers = mothers;
	}

	public ArrayList<NodeUser> getWives() {
		return wives;
	}

	public void addWife(NodeUser wife) {
		this.wives.add(wife);
	}

	public void setWives(ArrayList<NodeUser> wives) {
		this.wives = wives;
	}

	public ArrayList<NodeUser> getHusbands() {
		return husbands;
	}

	public void addHusbands(NodeUser husband) {
		this.husbands.add(husband);
	}

	public void setHusbands(ArrayList<NodeUser> husbands) {
		this.husbands = husbands;
	}

	public ArrayList<NodeUser> getBrothers() {
		return brothers;
	}

	public void addBrother(NodeUser brother) {
		this.brothers.add(brother);
	}

	public void setBrothers(ArrayList<NodeUser> brothers) {
		this.brothers = brothers;
	}

	public ArrayList<NodeUser> getSisters() {
		return sisters;
	}

	public void addSister(NodeUser sister) {
		this.sisters.add(sister);
	}

	public void setSisters(ArrayList<NodeUser> sisters) {
		this.sisters = sisters;
	}

	public ArrayList<NodeUser> getSons() {
		return sons;
	}

	public void addSon(NodeUser son) {
		this.sons.add(son);
	}

	public void setSons(ArrayList<NodeUser> sons) {
		this.sons = sons;
	}

	public ArrayList<NodeUser> getDaughters() {
		return daughters;
	}

	public void addDaughter(NodeUser daughter) {
		this.daughters.add(daughter);
	}

	public void setDaughters(ArrayList<NodeUser> daughters) {
		this.daughters = daughters;
	}

	public int getRelationCount() {
		return relationCount;
	}

	public void setRelationCount(int relationCount) {
		this.relationCount = relationCount;
	}

	private NodeUser setNodeUser(JSONObject nodeUser) {
		NodeUser tempNodeUser = new NodeUser();
		try {
			tempNodeUser.id = nodeUser.getInt("id");
			tempNodeUser.setFirstName(nodeUser.getString("firstname"));
			tempNodeUser.setLastName(nodeUser.getString("lastname"));
			tempNodeUser.setImage("http://www.parivartree.com/profileimages/thumbs/" + tempNodeUser.getId()
					+ "PROFILE.jpeg");
			tempNodeUser.setGender(Integer.parseInt(nodeUser.getString("gender")));
			tempNodeUser.setDeceased(Integer.parseInt(nodeUser.getString("deceased")));
			// tempNodeUser.setRelationId(Integer.parseInt(nodeUser.getString("relation")));
			tempNodeUser.setRelationCount(Integer.parseInt(nodeUser.getString("relationcount")));
			// tempNodeUser.setName(firstName); = this.firstName + " " +
			// this.lastName;
			// UrlImageViewHelper.setUrlDrawable(tempNodeUser.userImage,
			// "http://example.com/image.png", null, 60000);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		return tempNodeUser;
	}

}
