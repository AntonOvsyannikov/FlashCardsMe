package Model;
/*
TODO: сделать фичи

1) запоминаем на какой карте остановились
2) когда кладем карту в стек - открываем стек на ней
3) подумать или над дополнительным "сортировочным" стеком или как класть обратно в колоду например назад
4) анду перемещения карт последнего
5) поиск в картах
6) значек флильтеред
7) падает если убирать из фаворитесов через диалог
8) редактирование рекордов из диалога с картами
9) убрать экспорт-импорт дек в едит

*/
//TODO: Model is bad
// 1) DB is not necessary, more easy to work with serializable collections, flushing on disk time after time
// 2) Need to tune design, choose appropriate design pattern to avoid differnt instances reference single real-world object, like deck
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
import Model.Model.IProgress;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.lang.*;
import org.mapdb.*;
import java.util.concurrent.*;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


// ===============================================================

public class Model {
    
    // ===============================================================
    
    public class Exception extends java.lang.Exception {
        Exception(String s) { super(s); }
    }

    // ===============================================================

    public interface IProgress {
        void setProgress2(int i);
        boolean isCancelled2();
        int showOptionDialog2(String message, String title, String[] buttons, String selected);
    }
    
    // ===============================================================
    // idiotic helper class for workaround final variables in stream operations
    
    class NextI {
        int i=0;
        NextI(int i) { this.i = i; }
        int nextI() { return i++; }
    };

    // ===============================================================

    public enum MoveCardsOrder {FROM_TOP, FROM_BOTTOM, RANDOM};
    
    // ===============================================================

    public static class CardTemplateField implements Serializable{
        public String action;
        public String fontSize;
        public String color;
        public String prompt;
        public int order;
        public CardTemplateField() {
            this.action = "Hide";
            this.fontSize = "30";
            this.color = "Black";
            this.prompt = "";
            this.order = 0;
        }
    }

    // ===============================================================
    // Decks API
    
    String getDecksDirectory(){
        return System.getProperty("user.dir")+"/decks/";
    }

    public List<Deck> listDecks(){
        ArrayList<Deck> decks = new ArrayList<>();
        try {
            Files.list(Paths.get(getDecksDirectory()))
                    .forEach(filePath -> decks.add(new Deck(filePath.getFileName().toString())));
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        return decks;
    }
    
    public Deck createDeck(String name, Deck copyFrom) throws IOException, Exception {
        if (name == null || name.equals("")) throw new IllegalArgumentException();
        File newDir =new File(getDecksDirectory() + name); 
        if (newDir.exists()) throw new Exception ("Deck already exist");
        if (!newDir.mkdirs()) throw new Exception("Can not create/copy deck");
        if (copyFrom!=null)
            return new Deck(name).copy(copyFrom);
        else
            return new Deck(name).create();
    }
    
    public void deleteDeck(Deck deck) throws IOException {
        if (deck.isOpened()) throw new IllegalStateException();
        String deckName = deck.getName();
        MyIOUtils.removeRecursive(Paths.get(getDecksDirectory() + deckName + "/"));
    }
    
    public void renameDeck(Deck deck, String newName) throws IOException {
        if (deck.isOpened()) throw new IllegalStateException();
        if (newName.equals("")) throw new IllegalArgumentException();
        String deckName = deck.getName();
        File oldDir = new File(getDecksDirectory() + deckName);
        File newDir = new File(getDecksDirectory() + newName);
        if (newDir.exists()) throw new IOException("Deck with this name already exist");
        if (!oldDir.renameTo(newDir)) throw new IOException("Can not rename deck");
        deck.rename(newName);
    }
    
    public void exportDeck(Deck deck, File f, IProgress p) throws FileNotFoundException, IOException {
        if (deck.isOpened()) throw new IllegalStateException();
        
        Path deckDirectory = Paths.get(deck.getDeckDirectory());
        File versionFile = new File (deck.getDeckDirectory()+"version");
        File deckDataFile = new File (deck.getDeckDirectory()+"deck_data");
        Path mediaDirectory = Paths.get(deck.getDeckMediaDirectory());
        
        p.setProgress2(0);

        // write export version info
        try(  PrintWriter out = new PrintWriter( versionFile )  ){
            out.print(1);
        }
        

        // version 1 deck expoerter
        ExportDeckData edd = new ExportDeckData();
        edd.storeDeck(deck);
       
        FileOutputStream fos = new FileOutputStream(deckDataFile);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(edd);
            oos.flush();
        }
        
        p.setProgress2(50);
        
        // gzip everything into output file, including media
        try (ZipUtil.ZipOutputFile zof = new ZipUtil.ZipOutputFile(f)) {
            zof.addFile(versionFile, deckDirectory);
            zof.addFile(deckDataFile, deckDirectory);
            if (Files.exists(mediaDirectory)) zof.addDirectory(mediaDirectory, deckDirectory);
        }

        Files.delete(versionFile.toPath());
        Files.delete(deckDataFile.toPath());
        
        p.setProgress2(100);
    }
    
    public void importDeck(Deck deck, File f, IProgress p) throws IOException, Exception {
        if (deck.isOpened()) throw new IllegalStateException();

        Path deckDirectory = Paths.get(deck.getDeckDirectory());
        
        p.setProgress2(0);

        ZipUtil.unZipFile(f, deckDirectory);
        
        File versionFile = new File (deck.getDeckDirectory()+"version");

        List<String> lines = Files.readAllLines(versionFile.toPath());
        if (lines.isEmpty()) throw new Exception("Can not import: incorrect version file");
        if (!lines.get(0).equals("1")) throw new Exception("Can not import: incorrect version");
        
        p.setProgress2(50);

        
        File deckDataFile = new File (deck.getDeckDirectory()+"deck_data");
        
        ExportDeckData edd;

        FileInputStream fis = new FileInputStream(deckDataFile);
        try (ObjectInputStream ois = new ObjectInputStream(fis)) {
            edd = (ExportDeckData)ois.readObject();
            edd.restoreDeck(deck);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        Files.delete(versionFile.toPath());
        Files.delete(deckDataFile.toPath());
        
        p.setProgress2(100);

    }
                
    
    //===============================================================

    public class Deck {

        String deckName;
        
        Map mapProps;
        Map <String, Integer> mapFields;
        Map <Integer, Integer> mapRecords;
        Map <Integer, String> mapTemplates;
        Map <Integer, String> mapLearningSessions;
        Map <Integer, String> mapCardClasses;

        final String getTablePrefix() { return "deck"; }

        int generateID(String ofWhat){
            String property_name = "last_" + ofWhat + "_id";
            Object o = mapProps.get(property_name);
            int id = 0;
            if (o != null) id = (int)o;
            mapProps.put(property_name, id + 1);
            return id;
        }

        Deck (String deckName) { 
            if (deckName.equals("")) throw new IllegalArgumentException();
            this.deckName = deckName; 
        }
        
        public void open() {
            dbOpen();
            mapProps = dbMap(getTablePrefix() + "_props");
            mapFields = dbMap(getTablePrefix() + "_fields");
            mapRecords = dbMap(getTablePrefix() + "_records");
            mapTemplates = dbMap(getTablePrefix() + "_templates");
            mapLearningSessions = dbMap(getTablePrefix() + "_ls");
            mapCardClasses = dbMap(getTablePrefix() + "_cardclasses");
        }
        
        public void close() {
            mapProps = null;
            mapFields = null;
            mapRecords = null;
            mapTemplates = null;
            mapLearningSessions = null;
            mapCardClasses = null;
            dbClose();
        }
        
        boolean isOpened() {
            return dbIsOpened();
        }
        
        public void setDescription (String description) {
            mapProps.put("description", description);
        }
        
        public String getDescription () {
            Object o = mapProps.get("description");
            return (o==null) ? "" : (String)o;
        }

        @Override
        public String toString() { return deckName; }

        @Override
        public boolean equals(Object t) {
            if (t == null) return false;
            if (!(t instanceof Deck)) return false;
            return this.deckName.equals(((Deck)t).deckName);
        }
        
        Deck create() {
            open();
            mapProps.put("name", deckName);
            dbCommit();
            close();
            return this;
        }
        
        Deck copy(Deck copyFrom) throws IOException {
            copyFrom.dbCopyTo(deckName);
            return this;
        }
        
        public String getName() { return deckName; }
        
        void delete() {
            deckName = null;
        }
        
        void rename(String newName) {
            deckName = newName;
            open();
            mapProps.put("name", deckName);
            dbCommit();
            close();
        }
        
        public String getDeckDirectory() {
            return getDecksDirectory() + deckName +"/";
        }
        public String getDeckMediaDirectory() {
            return getDeckDirectory() + "media/";
        }

        // ===============================================================
        // CardClasses API
        
        String getCardClassProperty(CardClass t) {
            return mapCardClasses.get(t.getID());
        }
        void setCardClassProperty(CardClass t, String prop) {
            mapCardClasses.put(t.getID(), prop);
        }

        public CardClass createCardClass(String name) {
            int id = generateID("cardclass");
            mapCardClasses.put(id, "");
            return new CardClass(id).create(name);
        }
        
        public void deleteCardClass(CardClass t) {
            int id = t.getID();
            t.delete();
            mapCardClasses.remove(id);
        }
        
        CardClass getCardClassByID(int id) {
            if (mapCardClasses.containsKey(id)) return new CardClass(id);
            return null;
        }
        
        public List<CardClass> listCardClasses(){
            
            return mapCardClasses.entrySet()
                    .stream()
                    .sorted(comparing(e -> e.getKey()))
                    .map(e -> new CardClass(e.getKey()))
                    .collect(toList());
        }

        // ===============================================================

        public class CardClass {
            
            Integer id;
            Map<String, String> mapContent;
            
            CardClass(int id) { 
                this.id = id; 
                mapContent = dbMap(getTablePrefix()+"_cardclasses_content");
            } 
            
            CardClass create(String name) { 
                setCardClassProperty(this, name);
                return this; 
            }
            
            void delete() { id = null; }

            int getID() { return id; }

            public String getName(){
                return getCardClassProperty(this);
            }
            
            public void setName(String name){
                setCardClassProperty(this, name);
            }
            
            public void setContent(Field f, String v) {
                mapContent.put(id.toString() + "_f", f.getName());
                mapContent.put(id.toString() + "_v", v);
            }
            
            public Field getField() {
                return getFieldByName(mapContent.get(id.toString() + "_f"));
            }
            
            public String getValue() {
                return mapContent.get(id.toString() + "_v");
            }
            
            public boolean isApplicable(Card card) {
                return getValue().equals(card.record.getFieldContent(getField()));
            }
                        
            @Override
            public String toString() { return getName(); }
            
            @Override
            public boolean equals(Object t) {
                if (t == null) return false;
                if (!(t instanceof CardClass)) return false;
                return this.id == ((CardClass)t).id;
            }
            
            public Deck getDeck(){ return Deck.this; } 
            
            
            
        }        
        

        // ===============================================================
        // Filter for cards

        public class Filter {
            public boolean showAll = false;
            public List<Deck.Template> templates = new ArrayList<>();
            public List<Deck.CardClass> cardClasses = new ArrayList<>();
            
            boolean testAgainst(Card card) {
                if (showAll) return true;
                if (!templates.contains(card.template)) return false;
                if (cardClasses.isEmpty()) return true;
                for (CardClass cc : cardClasses) if(cc.isApplicable(card)) return true;
                return false;
            }

        }
        

        // ===============================================================
        // Fields API

        // creates field if not exist, otherwise returns field with this name
        public Field createFieldIfNotExist(String name) {
            if (mapFields.get(name) == null)
                mapFields.put(name, generateID("field"));
            return new Field(name).create();
        }

        public List<Field> listFields(){
            return mapFields.entrySet()
                    .stream()
                    .sorted(comparing(e->e.getValue()))
                    .map(entry -> new Field(entry.getKey()))
                    .collect(toList());
        }
        
        public List<Field> listVisibleFields(){
            return mapFields.entrySet()
                    .stream()
                    .sorted(comparing(e->e.getValue()))
                    .filter(e->!e.getKey().substring(0, 1).equals("*"))
                    .map(entry -> new Field(entry.getKey()))
                    .collect(toList());
        }
        
        public Field getFieldByName(String fieldName) {
            if (!mapFields.containsKey(fieldName)) return null;
            return new Field(fieldName);
        }
        
        public void deleteField(Field f) {
            listRecords().forEach(r->r.removeFieldContent(f));
            String name = f.getName();
            f.delete();
            mapFields.remove(name);
        }
        
        // if field with such name already exist - it's content will be overwritten by content of renaming fields
        public void setFieldName(Field f, String newName) {
            String name = f.getName();
            Integer o = mapFields.get(name);
            mapFields.remove(name);
            mapFields.put(newName, o);
            List<Record> records = listRecords();
            records.forEach(r -> r.renameFieldInContent(f, newName));
            f.setName(newName);
        }

        //===============================================================
        // trivia warp around name 
        
        public class Field {
            
            String name;
            
            Field(String name){ this.name = name; }

            Field create(){ return this; }
            
            void delete() { name = null; }
            
            public String getName() { return name; }
            
            void setName(String newName) { name = newName; }
            
            @Override
            public String toString() { return getName(); }
        }

        // ===============================================================
        // Records API
        
        public Record createRecord(){
            int id = generateID("record");
            mapRecords.put(id, 0);
            return new Record(id).create();            
        }
        
        public void deleteRecord(Record r) {
            int id = r.getID();
            r.delete();
            mapRecords.remove(id);
        }

        public List<Record> listRecords() {
            return mapRecords.entrySet()
                    .stream()
                    .sorted(comparing(e -> e.getKey()))
                    .map(entry -> new Record(entry.getKey()))
                    .collect(toList());
        }
        
        // ===============================================================
        
        public class Record {
            
            Integer id;
            Map <String, String> mapContent;

            String getTablePrefix() { return Deck.this.getTablePrefix(); }
            
            String getContentRecordID(String fieldName) {  return "record_" + id + "_" + fieldName; }
            
            Record(int id) { 
                this.id = id; 
                mapContent = dbMap(getTablePrefix() + "_content");
            } 
                        
            Record create(){ return this; }

            void delete() { 
                listFields().forEach(f->removeFieldContent(f));
                id = null; 
            }
            
            int getID() { return id; }
           
            public Deck getDeck(){ return Deck.this; } 

            // ============== API

            public void setFieldContent(Field f, String content) {
                mapContent.put(getContentRecordID(f.getName()), content);
            }

            public String getFieldContent(Field f) {
                String s =mapContent.get(getContentRecordID(f.getName()));
                return s == null?"":s;
            }

            public void removeFieldContent(Field f) {
                mapContent.remove(getContentRecordID(f.getName()));
            }

            public void renameFieldInContent(Field f, String newName) {
                mapContent.put(getContentRecordID(newName), mapContent.remove(getContentRecordID(f.getName())));
            }
        }        
            
        // ===============================================================
        // Templates API
        
        String getTemplateProperty(Template t) {
            return mapTemplates.get(t.getID());
        }
        void setTemplateProperty(Template t, String prop) {
            mapTemplates.put(t.getID(), prop);
        }

        public Template createTemplate(String name) {
            int id = generateID("template");
            mapTemplates.put(id, "");
            return new Template(id).create(name);
        }
        
        public void deleteTemplate(Template t) {
            int id = t.getID();
            t.delete();
            mapTemplates.remove(id);
        }
        
        Template getTemplateByID(int id) {
            if (mapTemplates.containsKey(id)) return new Template(id);
            return null;
        }
        
        public List<Template> listTemplates(){
            
            return mapTemplates.entrySet()
                    .stream()
                    .sorted(comparing(e -> e.getKey()))
                    .map(e -> new Template(e.getKey()))
                    .collect(toList());
        }

        // ===============================================================

        public class Template {
            
            Integer id;
            Map<String, CardTemplateField> mapTemplateFields;
            
            Template(int id) { 
                this.id = id; 
                this.mapTemplateFields = dbMap(getTablePrefix() + "_template_fields");
            } 
            
            Template create(String name) { 
                setTemplateProperty(this, name);
                return this; 
            }
            
            void delete() { id = null; }

            int getID() { return id; }
            
            public String getName(){
                return getTemplateProperty(this);
            }
            
            public void setName(String name){
                setTemplateProperty(this, name);
            }
            
            @Override
            public String toString() { return getName(); }
            
            @Override
            public boolean equals(Object t) {
                if (t == null) return false;
                if (!(t instanceof Template)) return false;
                return this.id == ((Template)t).id;
            }
            
            public void putTemplateField(boolean isFront, String fieldName, CardTemplateField field ){
                mapTemplateFields.put(Integer.toString(id)+"_" + (isFront?"front":"back") + "_" + fieldName, field);
            }
            
            public CardTemplateField getTemplateField(boolean isFront, String fieldName){
                CardTemplateField field = mapTemplateFields.get(Integer.toString(id)+"_" + (isFront?"front":"back") + "_" + fieldName);
                if (field == null) field = new CardTemplateField();
                return field;
            }

            public Deck getDeck(){ return Deck.this; } 

        }        

        // ===============================================================
        // Cards API
        
        public List<Card> listAllCards() {
            List<Record> recs = listRecords();
            List<Template> tpls = listTemplates();
/*            
            return recs.stream()
                    .flatMap(r -> tpls.stream()
                            .map(t->new Card(r,t)))
                    .collect(toList());
*/
            List<Card> list = new ArrayList<>();
            for (Record r:recs)
                for (Template t:tpls)
                    list.add(new Card(r,t));
            return list;
        }

        // ===============================================================

        public class Card implements Comparable {
            public Record record;
            public Template template;
            public Stack stack = null;
            
            public Card (Record record, Template template) {
                this.record = record;
                this.template = template;
            }

            // created from id, which looks like "r12312t2"
            Card (String id) {
                Pattern p = Pattern.compile("r(\\d+)t(\\d+)");
                Matcher m=p.matcher(id);
                if (!m.matches()) throw new IllegalArgumentException();
                int record_id = Integer.parseInt(m.group(1));
                int template_id = Integer.parseInt(m.group(2));
                this.record = new Record(record_id);
                this.template = new Template(template_id);
            }
            
            String getID() {
                return "r"+Integer.toString(record.id)+"t"+Integer.toString(template.id);
            }
            
            public Deck getDeck(){ return Deck.this; } 
            
            @Override
            public boolean equals(Object o) {
                if (o == null) return false;
                if (!(o instanceof Card)) return false;
                return this.getID().equals(((Card)o).getID());
            }

            @Override
            public int compareTo(Object o) {
                return this.getID().compareTo(((Card)o).getID());
            }
        }
        

        // ===============================================================
        // Learning Sessions API

        String getLearningSessionProperty(LearningSession ls) {
            return mapLearningSessions.get(ls.getID());
        }
        void setLearningSessionProperty(LearningSession ls, String prop) {
            mapLearningSessions.put(ls.getID(), prop);
        }

        public LearningSession createLearningSession(String name) {
            int id = generateID("learning_session");
            mapLearningSessions.put(id, "");
            return new LearningSession(id).create(name);
        }
        
        public void deleteLearningSession(LearningSession ls) {
            int id = ls.getID();
            ls.delete();
            mapLearningSessions.remove(id);
        }
        
        public List<LearningSession> listLearningSessions(){
            return mapLearningSessions.entrySet()
                    .stream()
                    .sorted(comparing(e -> e.getKey()))
                    .map(e -> new LearningSession(e.getKey()))
                    .collect(toList());
        }

        // ===============================================================

        public class LearningSession {
            
            Integer id = null;
            Map<String, Integer> mapStacks = null;
            Map mapStat = null;
            Map mapProps = null;
            Map<String, String>  mapCardEvents = null;
            
            String getTablePrefix() { return "ls_"+Integer.toString(id); }

            LearningSession(int id) { 
                this.id = id; 
                mapStacks = dbMap(getTablePrefix()+"_stacks");
                mapStat = dbMap(getTablePrefix()+"_stat");
                mapProps = dbMap(getTablePrefix()+"_props");
                mapCardEvents = dbMap(getTablePrefix()+"_cardevents");
                
            } 

            LearningSession create(String name){
                setLearningSessionProperty(this, name);
                return this;
            }
            
            void delete(){
                listStacks().forEach(stack->stack.delete());
                mapStacks = null;
                dbDeleteTable(getTablePrefix()+"_stacks");
                mapStat = null;
                dbDeleteTable(getTablePrefix()+"_stat");
                mapCardEvents = null;
                dbDeleteTable(getTablePrefix()+"_cardevents");
                id = null;
            }
            
            int getID() { return id; }
            
            public String getName(){
                return getLearningSessionProperty(this);
            }
            
            public void setName(String name){
                setLearningSessionProperty(this, name);
            }

            public Deck getDeck(){ return Deck.this; } 
            
            @Override
            public String toString() { return getName(); }

            @Override
            public boolean equals(Object t) {
                if (t == null) return false;
                if (!(t instanceof LearningSession)) return false;
                if (!((LearningSession)t).getDeck().equals(this.getDeck())) return false; 
                return this.id == ((LearningSession)t).id;
            }

            // ===============================================================
            // Stacks API
            
            public Stack createStack(String name, boolean custom) throws Exception {
                if (mapStacks.containsKey(name)) throw new Exception("Stack with this name already exist");
                mapStacks.put(name, generateID("ls_"+ Integer.toString(this.id)+"_stack"));
                return new Stack(name).create(custom);
            }

            public Stack getStack(String name) {
                if (mapStacks.containsKey(name)) return new Stack(name);
                return null;
            }
            
            public List<Stack> listStacks() {
                return mapStacks.entrySet()
                        .stream()
                        .sorted(comparing(e -> e.getValue()))
                        .map(entry -> new Stack(entry.getKey()))
                        .collect(toList());
            }
/*            
            public void setStackName(Stack stack, String newName) throws Exception {
                if (mapStacks.containsKey(newName)) throw new Exception("Stack with this name already exist");
                Integer i = mapStacks.remove(stack.name);
                mapStacks.put(newName, i);
                stack.setName(newName);
            }
*/            
            public void deleteStack(Stack stack) {
                if (!stack.isCustom()) throw new IllegalArgumentException();
                if (stack.getCardsNumber()>0) throw new IllegalArgumentException();
                String stackName = stack.getName();
                stack.delete();
                mapStacks.remove(stackName);
            }
            
            // ===============================================================

            public class Stack {

                String name;
                Map<String, Integer> mapCards;
                Map<String, Boolean> mapCardsVisible;
                Map mapProps;
                Map<Long, Integer> mapStat;
                
                final String getTablePrefix() { return LearningSession.this.getTablePrefix() + "_stack_" + name; }

                <V> void setProperty(String propertyName, V value) {
                    mapProps.put(propertyName, value);
                }

                <V> V getProperty(String propertyName, V defaultValue) {
                    Object o = mapProps.get(propertyName);
                    if (o == null) {
//                        mapProps.put(propertyName, defaultValue);
                        return defaultValue;
                    }
                    return (V)o;
                }
                
                Stack (String name) { 
                    if (name == null || "".equals(name)) throw new IllegalArgumentException();
                    this.name = name;
                    mapCards = dbMap(getTablePrefix()+"_cards");
                    mapCardsVisible = dbMap(getTablePrefix()+"_cards_visible");
                    mapProps = dbMap(getTablePrefix()+"_props");
                    mapStat = dbMap(getTablePrefix()+"_stat");
                }
                
                @Override
                public String toString() { return name; }
                
                @Override
                public boolean equals(Object t) {
                    if (t == null) return false;
                    if (!(t instanceof Stack)) return false;
                    if (!((Stack)t).getLearningSession().equals(this.getLearningSession())) return false; 
                    return this.name.equals(((Stack)t).name);
                }

                public LearningSession getLearningSession() { return LearningSession.this; } 
                
                Stack create(boolean custom) { 
                    setProperty("custom", custom);
                    setProperty("favorities", false);
                    clear();
                    return this; 
                }
                
                // TODO not so good, too interfacy thing to put in model
                // "links" stacks to display on left/right button of the card dialog
                // note then "left" stack is not always "right" for other and vise versa
                public void linkStack(Stack stackLinked, boolean isLeft) {
                    setProperty("linked_" + (isLeft?"left":"right"), stackLinked.getName());
                }

                public Stack getLinked (boolean isLeft) {
                    String name = getProperty("linked_" + (isLeft?"left":"right"), null);
                    return name == null ? null : new Stack(name);
                }
                
                public boolean isCustom () {
                    return getProperty("custom", false);
                }
                
                public boolean isFavorities () {
                    return getProperty("favorities", false);
                }
                
                public void setIsFavorities(boolean fav) {
                    setProperty("favorities", fav);
                }
                // ====
               
                void delete() { 
                    mapCards = null; 
                    dbDeleteTable(getTablePrefix()+"_cards"); 
                    mapCardsVisible = null; 
                    dbDeleteTable(getTablePrefix()+"_cards_visible"); 
                    mapProps = null; 
                    dbDeleteTable(getTablePrefix()+"_props"); 
                    mapStat = null; 
                    dbDeleteTable(getTablePrefix()+"_stat"); 
                    name = null;
                }

                public String getName() { 
                    return name; 
                }
                
                void setName(String newName)  {
                    name = newName;
                }
                
                public List<Card> listCards() {
                    return mapCards.entrySet().stream()
                            .sorted(comparing(e -> e.getValue()))
                            .map(e->new Card(e.getKey()))
                            .collect(toList());
                }
                
                public int getCardsNumber() {
                    return mapCards.size();
                }
                
                public List<Card> listVisibleCards() {
                    return mapCards.entrySet().stream()
                            .sorted(comparing(e -> e.getValue()))
                            .filter(e->mapCardsVisible.get(e.getKey())!=null)
                            .map(e->new Card(e.getKey()))
                            .collect(toList());
                }
                
                public int getVisibleCardsNumber() {
                    return mapCardsVisible.size();
                }
                
                public void clear() {
                    mapCards.clear();
                    mapCardsVisible.clear();
                    mapProps.put("min_order", 0);
                    mapProps.put("max_order", 0);
                }
                
                // elemetary operation, cards orders are not consistent after calling this
                void _addCard(Card card, boolean toFront) {
                    Filter filter = getFilter();
                    if (toFront) {
                        int minOrder = (int)mapProps.get("min_order");
                        minOrder--;
                        mapCards.put(card.getID(), minOrder);
                        if (filter.testAgainst(card)) mapCardsVisible.put(card.getID(), true);
                        mapProps.put("min_order", minOrder);
                    } else {
                        int maxOrder = (int)mapProps.get("max_order");
                        mapCards.put(card.getID(), maxOrder);
                        if (filter.testAgainst(card)) mapCardsVisible.put(card.getID(), true);
                        maxOrder++;
                        mapProps.put("max_order", maxOrder);
                    }
                }
                
                // elemetary operation, cards orders are not consistent after calling this
                void _removeCard(Card card) {
                    mapCards.remove(card.getID());
                    mapCardsVisible.remove(card.getID());
                }

                // elemetary operation, cards orders are not consistent after calling this
                void setOrder(Card card, int order) {
                    mapCards.put(card.getID(), order);
                }
                
                void applyFilter(Filter filter) {
                    listCards().forEach(card-> { 
                        if (filter.testAgainst(card))
                            mapCardsVisible.put(card.getID(), true);
                        else
                            mapCardsVisible.remove(card.getID());
                    });
                }
                
                void compressOrder() {
                    NextI ni = new NextI(0);
                    listCards().forEach(card->setOrder(card, ni.nextI()));
                    mapProps.put("min_order", 0);
                    mapProps.put("max_order", mapCards.size());
                }

                public void addCard(Card card, boolean toFront) {
                    _addCard(card, toFront);
                    compressOrder();
                }
                
                public void removeCard(Card card) {
                    _removeCard(card);
                    compressOrder();
                }

                public int getOrder(Card card) {
                    return mapCards.get(card.getID());
                }
                
                public boolean contains(Card card) {
                    return mapCards.containsKey(card.getID());
                }
                
                public void shuffle() {
                    Random r = new Random();
                    listCards().forEach(card->setOrder(card, r.nextInt()));
                    compressOrder();
                }
                
                // ===============================================================
                // stat
                
            
                public Map<Long, Integer> statGetEvents() {
                    return new HashMap(mapStat);
                }

                void statRegisterEvent() {
                    Date date = new Date();
                    long eventStamp = date.getTime() / 1000; // TODO: /60/60/24 actually
                    mapStat.put(eventStamp*1000, getCardsNumber());
                }
            
            }
            
            // ===============================================================
            // cards manipulations
            // TODO: actually no need compress order mechanism, cause cards are added only to beginning or the end of stack
            
            public void updateCards(Stack stackAddNewCardsTo) {

                List<Stack> stacks = listStacks();
                Map<Card, Stack> mapCardToStack = new TreeMap<>();
                stacks.forEach(stack -> stack.listCards().forEach(card -> mapCardToStack.put(card, stack)));

                List<Card> allCards = listAllCards();
                allCards.forEach(card -> { if (!mapCardToStack.containsKey(card)) stackAddNewCardsTo._addCard(card, false);} );
                
                mapCardToStack.forEach( (card, stack) -> { if (!allCards.contains(card)) stack._removeCard(card);} );
                stacks.forEach(stack-> { stack.compressOrder(); stack.statRegisterEvent();} );
            }
            //TODO: moveOrCopyCard(..)
            
            
            public int moveCards(Stack stackFrom, Stack stackTo, int cardsNumber, MoveCardsOrder order) {
                List<Model.Deck.Card> cards = stackFrom.listVisibleCards();
                int nMoved = 0;
                for (int i = 0; i < cardsNumber && cards.size() > 0; i++) {
                    int n = 0;
                    switch (order) {
                        case FROM_TOP:
                            n=0;
                            break;
                        case FROM_BOTTOM:
                            n=cards.size()-1;
                            break;
                        case RANDOM:
                            Random r = new Random();
                            n = r.nextInt(cards.size());
                            break;
                    }
                    Model.Deck.Card card = cards.remove(n);
                    stackFrom._removeCard(card);
                    stackTo._addCard(card, true);
                    statRegisterCardEvent(card, stackTo);
                    nMoved++;
                }
                stackFrom.statRegisterEvent();
                stackTo.statRegisterEvent();
                
                if (nMoved > 0) {
                    stackFrom.compressOrder();
                    stackTo.compressOrder();
                }
                return nMoved;
            }

            // ===============================================================
            // cards stats

            <T> void setCardStat(Card card, String valueName, T value){
                mapStat.put(card.getID() + "_" + valueName, value);
            }
            
            <T> T getCardStat(Card card, String valueName, T defaultValue){ 
                T value = (T) mapStat.get(card.getID() + "_" + valueName);
                if (value == null) {
                    value = defaultValue;
//                    mapStat.put(card.getID() + "_" + valueName, value);
                }
                return value;
            }
            
            // register in which stack card was moved this day
            void statRegisterCardEvent(Card card, Stack stackTo) {
                Date date = new Date();
                long eventStamp = date.getTime() / 1000; // TODO: /60/60/24 actually
                mapCardEvents.put(card.getID() + "_event_" + Long.toString(eventStamp*1000), stackTo.getName());
            }
            
            public Map<Long, String> statGetCardEvents(Card card) {
                Map<Long,String> events = new HashMap();
                Pattern p = Pattern.compile(card.getID()+"_event_(\\d*)");
                
                for (Map.Entry<String,String> e : mapCardEvents.entrySet()) {
                    Matcher m = p.matcher(e.getKey());
                    if (m.matches())
                        events.put(Long.parseLong(m.group(1)), e.getValue());
                }
                
                return events;
                
/*                mapStat.entrySet().stream()
                        .filter(e->e.getKey())
*/
            }
            
            public void statCardShown(Card card) {
                setCardStat(card, "shows", getCardStat(card, "shows", 0)+1);
                setCardStat(card, "shows_in_stack", getCardStat(card, "shows_in_stack", 0)+1);
            }
            
            public void statCardMoved(Card card, Stack stackFrom, Stack stackTo) {
                setCardStat(card, "shows_in_stack", 0);
                setCardStat(card, "success", 0);
                statRegisterCardEvent(card, stackTo);
                stackFrom.statRegisterEvent();
                stackTo.statRegisterEvent();
            }
            
            public void statCardAnswered(Card card, boolean isSuccess) {
                int c = getCardStat(card, "success", 0);
                setCardStat(card, "success", isSuccess?(c<0?0:c+1):c-1);
            }
            
            public int statGetShowsCount(Card card, boolean isTotalOrInStack) {
                return isTotalOrInStack?getCardStat(card, "shows", 0):getCardStat(card, "shows_in_stack", 0);
            }
            
            public int statGetSuccessCounter(Card card) {
                return getCardStat(card, "success", 0);
                
            }
/*

                if (currentFace){
                    sc++;
                    ls.setCardStat(currentCard, "shows", sc);
                    scs++;
                    ls.setCardStat(currentCard, "shows_in_stack", scs);
                } else {
                    if (b.b) {
                        if (scnt < 0) scnt = 0; else scnt++;
                    } else {
                        scnt--;
                    }
                    ls.setCardStat(currentCard, "success", scnt);
                }

            
            
*/            
                        
            // ===============================================================
            // learning sessions properties
            
            <V> void setProperty(String propertyName, V value) {
                mapProps.put(propertyName, value);
            }
            
            <V> V getProperty(String propertyName, V defaultValue) {
                Object o = mapProps.get(propertyName);
                if (o == null) {
                    mapProps.put(propertyName, defaultValue);
                    return defaultValue;
                }
                return (V)o;
            }
            
            // ===============================================================
            // Filter cards
            

            public void applyFilter(Filter filter) {
                setProperty("showAll", filter.showAll);
                setProperty("templates", filter.templates.stream()
                    .map(t->Integer.toString(t.getID()))
                    .collect(joining(" "))
                );
                setProperty("cardClasses", filter.cardClasses.stream()
                    .map(t->Integer.toString(t.getID()))
                    .collect(joining(" "))
                );
                listStacks().forEach(s->s.applyFilter(filter));
            }
            
            public Filter getFilter() {
                
                Filter filter = new Filter();
                
                filter.showAll = getProperty("showAll", true);
                
                String templates = getProperty("templates", "");
                if (!"".equals(templates)) filter.templates = Arrays.stream(templates.split(" "))
                        .map(s->getTemplateByID(Integer.parseInt(s)))
                        .filter(t->t!=null)
                        .collect(toList());
                
                String ccs = getProperty("cardClasses", "");
                if (!"".equals(ccs)) filter.cardClasses = Arrays.stream(ccs.split(" "))
                        .map(s->getCardClassByID(Integer.parseInt(s)))
                        .filter(t->t!=null)
                        .collect(toList());
                
                return filter;
            }
            
        }
        
        // Import meidia
        public void importMedia(File[] files, IProgress p) throws IOException {
            String mediaDir = getDeckMediaDirectory();
            new File(mediaDir).mkdirs();
            Path dst = Paths.get(mediaDir);
            boolean replace_all = false;

            for (int i = 0; i < files.length && !p.isCancelled2(); i++) {
                p.setProgress2(100*i/(files.length-1));
                File file = files[i];
                if (replace_all)
                    Files.copy(file.toPath(), dst.resolve(file.getName()), REPLACE_EXISTING);
                else try {
                    Files.copy(file.toPath(), dst.resolve(file.getName()));
                } catch (FileAlreadyExistsException ex) {
                    String[] buttons = { "Replace", "Replace All", "Do not replace", "Cancel" };

//                                showOptionDialog(null, "File \"" + file.getName() + "\" already exist. Replace?", "File already exist", YES_NO_OPTION, QUESTION_MESSAGE, null, buttons, buttons[2])) {
                    switch(p.showOptionDialog2("File \"" + file.getName() + "\" already exist. Replace?", "File already exist", buttons, buttons[2])) {
                        case 1: 
                            replace_all = true;
                        case 0:
                            Files.copy(file.toPath(), dst.resolve(file.getName()), REPLACE_EXISTING);
                        case 2:
                            break;
                        case 3: return;
                    }
                }
            }

        }
        
        // Internal DB api
        // TODO: only one object should be created and worked with
        
        DB db;

        void dbOpen() {
            if (db!=null) throw new IllegalStateException();
            String filename = getDecksDirectory()+deckName+"/db";
            db = DBMaker
                .fileDB(new File(filename))
                .allocateStartSize(100 * 1024)
                .allocateIncrement(100 * 1024)
                .closeOnJvmShutdown()
                .make();
        }

        void dbClose(){
            if (db==null) throw new IllegalStateException();
            db.close();
            db = null;
        }
        
        boolean dbIsOpened() {
            return db!=null;
        }

        void dbCopyTo(String newName) throws IOException {
            if (db!=null) throw new IllegalStateException();

            Path src = Paths.get(getDecksDirectory()+deckName+"/db");
            Path dst = Paths.get(getDecksDirectory()+newName+"/db");
            Files.copy(src, dst, REPLACE_EXISTING);
        }

        final <K,V> Map<K,V> dbMap(String mapName){
            return db.hashMap(mapName);
        }

        void dbRenameTable(String oldName, String newName) {
            db.rename(oldName, newName);
        }

        void dbDeleteTable(String tableName){
            db.delete(tableName);
        }

        // db API
        // db should be commited explicitly by Controller
        public void dbCommit() {
            db.commit();
        }
        public void dbRollback() {
            db.rollback();
        }

        // helpers API for debugging
        public Set<String> dbListMaps() {
            return db.getAll().keySet();
        }
        
        public void dbClearTable(String name) {
            db.hashMap(name).clear();
        }

        public String dbQueryDB(String mapName){
           String result="";
           return db.hashMap(mapName).entrySet().stream()
                   .map(e->e.getKey().toString()+"="+e.getValue().toString())
                   .collect(joining("\n"));
        }

        public Integer dbQueryDB_getRecordsNumber(String mapName){
           return (int) db.hashMap(mapName).size();
        }


    }            

    static class ExportDeckData implements Serializable {
        
        class Template implements Serializable {
            String name;
            Map<Boolean, Map<String, CardTemplateField>> templateFields;
            Template(Deck.Template t) {
                name = t.getName();
                List<Deck.Field> fields = t.getDeck().listFields();
                templateFields = Arrays.stream(new Boolean[] {true, false})
                        .collect(toMap(
                                i -> i,
                                i -> fields.stream()
                                    .sorted((f1,f2)->(Integer.compare(t.getTemplateField(i, f1.getName()).order, t.getTemplateField(i, f2.getName()).order)))
                                    .collect(toMap(
                                        f -> f.getName(),
                                        f -> t.getTemplateField(i, f.getName())
                                    ))
                        ));
                
            }
        }
        
        class CardClass implements Serializable {
            String name, field, value;
            CardClass(Deck.CardClass cc) {
                name = cc.getName();
                field = cc.getField().getName();
                value = cc.getValue();
            }
        }
        
        List<String> fields;
        List<Map<String, String>> records;
        List<Template> templates;
        List<CardClass> cardClasses;
        String description;
        
        void storeDeck (Deck deck) {
            deck.open();
            fields = deck.listFields().stream().map(f->f.getName()).collect(toList());
            Deck.Record a;
                    
            records = deck.listRecords().stream()
                    .map(r -> deck.listFields().stream()
//                            .map(f -> new Map.Entry<String, String>(f.getName(),r.getFieldContent(f)))
                            .collect(toMap(
                                    f -> f.getName(),
                                    f -> r.getFieldContent(f)
                            ))
                    )
                    .collect(toList());
            
            templates = deck.listTemplates().stream()
                    .map(t -> new Template(t))
                    .collect(toList());
            
            cardClasses = deck.listCardClasses().stream()
                    .map(cc -> new CardClass(cc))
                    .collect(toList());
            
            description = deck.getDescription();
                    
            deck.close();
        }
        void restoreDeck (Deck deck) {
            deck.open();
            fields.forEach(f -> deck.createFieldIfNotExist(f));
            records.forEach(r -> {
                Deck.Record rr = deck.createRecord();
                r.entrySet().forEach(
                        e -> rr.setFieldContent(deck.getFieldByName(e.getKey()), e.getValue())
                );
            });
            
            templates.forEach(t -> {
                Deck.Template tt = deck.createTemplate(t.name);
                t.templateFields.entrySet().forEach(
                        e -> e.getValue().entrySet().forEach(
                                ee -> tt.putTemplateField(e.getKey(), ee.getKey(), ee.getValue())
                        )
                );
            });
            
            cardClasses.forEach(cc -> {
                Deck.CardClass ccc = deck.createCardClass(cc.name);
                ccc.setContent(deck.getFieldByName(cc.field), cc.value);
            });
            
            deck.setDescription(description);
            
            deck.dbCommit();
            deck.close();
        }
    }

}
