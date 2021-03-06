/*
 *  XMPP API to chat and interact with the League of Legends ChatServers. 
 *
 *  Copyright (C) 2017  Josue Acevedo
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package lolxmpp.api.data;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lolxmpp.api.RiotAPI;
import lolxmpp.api.exceptions.APIException;

/**
 * @author wirlie
 *
 */
public class ProfileIcon {
	
	private int id;
	private static Map<Integer, BufferedImage> profileImages = new HashMap<Integer, BufferedImage>();
	
	/**
	 * @deprecated Only for API initialization.
	 * @param api RiotAPI Key
	 */
	public static void loadProfileIcons(RiotAPI api) {
		try {
			JsonObject object = api.makeRequest("/lol/static-data/v3/profile-icons");
			if(object.has("version")) {
				String version = object.get("version").getAsString();
				try {
					BufferedImage fullImage = ImageIO.read(new URL("http://ddragon.leagueoflegends.com/cdn/" + version + "/img/sprite/profileicon0.png"));
					if(object.has("data")) {
						JsonObject data = object.get("data").getAsJsonObject();
						for(Entry<String, JsonElement> member : data.entrySet()) {
							try {
								JsonObject dataObject = data.get(member.getKey()).getAsJsonObject();
								
								int profileIconID = dataObject.get("id").getAsInt();
								
								JsonObject imageObject = dataObject.get("image").getAsJsonObject();
								
								int x = imageObject.get("x").getAsInt();
								int y = imageObject.get("y").getAsInt();
								int w = imageObject.get("w").getAsInt();
								int h = imageObject.get("h").getAsInt();
								
								profileImages.put(profileIconID, fullImage.getSubimage(x, y, w, h));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				} catch (IOException e) {
					System.err.println("Error : URL: " + "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/sprite/profileicon0.png");
					e.printStackTrace();
				}
			}
		} catch (APIException e1) {
			e1.printStackTrace();
		}
	}
	
	public ProfileIcon(int id) {
		if(id < 0) {
			id = 0;
		}
		
		this.id = id;
	}
	
	public BufferedImage getImage(int width, int height) {
		if(profileImages.containsKey(id)) {
			Image toolkitImage = profileImages.get(id).getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage buffImg = new BufferedImage(toolkitImage.getWidth(null), toolkitImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = (Graphics2D) buffImg.getGraphics();
			g2.drawImage(toolkitImage, 0, 0, null);
			g2.dispose();
			return buffImg;
		}
		
		System.err.println("ProfileIconID " + id + " not implemented.");
		
		return null;
	}
	
	public int getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProfileIcon[id=" + id + "]";
	}
}
