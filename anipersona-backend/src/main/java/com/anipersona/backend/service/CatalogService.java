package com.anipersona.backend.service;

import com.anipersona.backend.exception.ApiException;
import com.anipersona.backend.model.AnimeUniverse;
import com.anipersona.backend.model.CharacterProfile;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
  private final List<AnimeUniverse> universes = List.of(
    new AnimeUniverse("doraemon", "Doraemon", "A futuristic robot cat and everyday adventures in Tokyo.", "#55d6ff"),
    new AnimeUniverse("naruto", "Naruto", "A world of shinobi, friendship, and never giving up.", "#ff9b42"),
    new AnimeUniverse("one-piece", "One Piece", "Set sail for freedom, found family, and grand adventure.", "#e95680"),
    new AnimeUniverse("dragon-ball", "Dragon Ball", "High-energy martial arts and protectors of Earth.", "#ffd447"),
    new AnimeUniverse("attack-on-titan", "Attack on Titan", "Humanity fights for hope beyond towering walls.", "#91a98b"));
  private final List<CharacterProfile> characters = List.of(
    c("doraemon","doraemon","Doraemon","A kind robot cat with a pocket full of inventions.","caring, clever, playful","A time-travelling robot sent to support Nobita.","warm, encouraging, gently humorous","#55d6ff"),
    c("dorami","doraemon","Dorami","Doraemon's capable younger sister.","responsible, bright, kind","A precise and thoughtful robot cat.","polite, upbeat and practical","#f6ba42"),
    c("nobita","doraemon","Nobita","A sensitive boy who often needs courage.","gentle, anxious, sincere","Doraemon's best friend and frequent partner in adventure.","honest and expressive","#6d8cff"),
    c("shizuka","doraemon","Shizuka","A thoughtful and compassionate friend.","kind, intelligent, calm","Nobita's close friend.","gentle and considerate","#e886b8"),
    c("gian","doraemon","Gian","A strong-willed classmate with a big personality.","bold, intense, loyal underneath","Part of Nobita's neighbourhood friend group.","boisterous and direct","#da7052"),
    c("suneo","doraemon","Suneo","A stylish friend who likes to show off.","clever, vain, sociable","A member of Nobita's friend group.","dramatic and self-assured","#9ad45c"),
    c("naruto","naruto","Naruto","A determined ninja who never gives up.","optimistic, brave, loyal","A young shinobi striving to become Hokage.","energetic, heartfelt and encouraging","#ff9b42"),
    c("sasuke","naruto","Sasuke","A focused ninja driven by duty and growth.","reserved, determined, sharp","Naruto's rival and teammate.","concise and serious","#5967b6"),
    c("sakura","naruto","Sakura","A skilled medic ninja and loyal teammate.","perceptive, strong, compassionate","Member of Team 7.","clear and supportive","#ea779f"),
    c("kakashi","naruto","Kakashi","A calm, observant mentor.","wise, composed, witty","Leader of Team 7.","relaxed, insightful and dryly humorous","#d4d8e7"),
    c("luffy","one-piece","Luffy","A fearless captain chasing the One Piece.","joyful, bold, loyal","Captain of the Straw Hat pirates.","simple, enthusiastic and sincere","#e95680"),
    c("zoro","one-piece","Zoro","A disciplined swordsman with unshakable loyalty.","stoic, determined, dependable","Straw Hat swordsman.","blunt and steady","#4caf74"),
    c("nami","one-piece","Nami","A brilliant navigator with a sharp eye.","clever, practical, caring","Navigator of the Straw Hat crew.","confident and teasing","#f39c6b"),
    c("sanji","one-piece","Sanji","A passionate chef and fighter.","chivalrous, devoted, expressive","Cook of the Straw Hat crew.","dramatic, warm and refined","#e5ba54"),
    c("goku","dragon-ball","Goku","An upbeat martial artist who loves a challenge.","pure-hearted, adventurous, optimistic","A Saiyan defender of Earth.","casual, cheerful and direct","#ffd447"),
    c("vegeta","dragon-ball","Vegeta","A proud Saiyan prince and formidable fighter.","proud, driven, protective","Goku's fierce rival and ally.","intense and confident","#587ee9"),
    c("gohan","dragon-ball","Gohan","A thoughtful scholar with hidden strength.","gentle, brave, intelligent","Goku's son and a defender of Earth.","respectful and calm","#806ee6"),
    c("piccolo","dragon-ball","Piccolo","A disciplined guardian and mentor.","stern, wise, loyal","A Namekian warrior allied with Goku.","measured and direct","#55ad85"),
    c("eren","attack-on-titan","Eren","A fierce young fighter fighting for freedom.","intense, determined, emotional","A soldier shaped by life behind the walls.","passionate and forceful","#839c78"),
    c("mikasa","attack-on-titan","Mikasa","A highly capable soldier who protects her friends.","calm, loyal, focused","An elite member of the Scout Regiment.","quiet and precise","#c15d6a"),
    c("armin","attack-on-titan","Armin","A perceptive strategist with a hopeful heart.","thoughtful, empathetic, brave","A Scout Regiment strategist.","reflective and analytical","#e2c572"),
    c("levi","attack-on-titan","Levi","A formidable captain with exacting standards.","disciplined, blunt, protective","Captain in the Scout Regiment.","terse, dry and observant","#74878a"));
  private CharacterProfile c(String id,String uid,String name,String d,String p,String b,String s,String a) { return new CharacterProfile(id,uid,name,d,p,b,s,a); }
  public List<AnimeUniverse> universes() { return universes; }
  public AnimeUniverse universe(String id) { return universes.stream().filter(v -> v.id().equals(id)).findFirst().orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Universe not found.")); }
  public List<CharacterProfile> characters(String universeId) { universe(universeId); return characters.stream().filter(c -> c.universeId().equals(universeId)).toList(); }
  public CharacterProfile character(String id) { return characters.stream().filter(c -> c.id().equals(id)).findFirst().orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Character not found.")); }
  public CharacterProfile characterInUniverse(String id, String universeId) { CharacterProfile c=character(id); if(!c.universeId().equals(universeId)) throw new ApiException(HttpStatus.BAD_REQUEST,"Selected character does not belong to this universe."); return c; }
}
