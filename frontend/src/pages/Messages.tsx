import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { 
  MessageCircle, 
  Send, 
  User, 
  Clock, 
  Search,
  Plus,
  Mail,
  MailOpen
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { toast } from "@/hooks/use-toast";
import { messagesApi, userApi, type Conversation, type Message, type User as UserType, type NonAdvisersResponse } from "@/lib/api";

const messageSchema = z.object({
  recipientId: z.number().min(1, "Please select a recipient"),
  subject: z.string().min(1, "Subject is required"),
  content: z.string().min(1, "Message content is required"),
});

type MessageFormData = z.infer<typeof messageSchema>;

export const Messages = () => {
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [selectedConversation, setSelectedConversation] = useState<Conversation | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [users, setUsers] = useState<NonAdvisersResponse[]>([]);
  const [currentUser, setCurrentUser] = useState<UserType | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [newMessage, setNewMessage] = useState("");
  const [isSending, setIsSending] = useState(false);

  const form = useForm<MessageFormData>({
    resolver: zodResolver(messageSchema),
  });

  useEffect(() => {
    // Check if user is authenticated before fetching data
    const token = localStorage.getItem('auth_token');
    if (!token) {
      toast({
        variant: "destructive",
        title: "Authentication Required",
        description: "Please log in to access messages",
      });
      setIsLoading(false);
      return;
    }
    
    fetchData();
  }, []);

  useEffect(() => {
    if (selectedConversation) {
      fetchMessages(selectedConversation.userId);
    }
  }, [selectedConversation]);

  const fetchData = async () => {
    try {
      console.log('Fetching messages data...');
      console.log('Auth token:', localStorage.getItem('auth_token'));
      console.log('API base URL:', import.meta.env.VITE_API_URL || 'http://localhost:8081');
      
      const [conversationsResponse, usersResponse, currentUserResponse] = await Promise.all([
        messagesApi.getConversations(),
        userApi.getNonAdvisers(),
        userApi.getProfile(),
      ]);
      
      console.log('Conversations response:', conversationsResponse);
      console.log('Users response:', usersResponse);
      console.log('Current user response:', currentUserResponse);
      console.log('Current user data:', currentUserResponse.data);
      
      setConversations(conversationsResponse.data || []);
      setUsers(usersResponse.data || []);
      setCurrentUser(currentUserResponse.data);
    } catch (error: any) {
      console.error('Error fetching messages data:', error);
      console.error('Error details:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status,
        statusText: error.response?.statusText,
        config: error.config
      });
      
      // Check if it's an authentication error
      if (error.response?.status === 401) {
        toast({
          variant: "destructive",
          title: "Authentication Error",
          description: "Please log in again to access messages",
        });
        return;
      }
      
      // Check if it's a network error
      if (!error.response) {
        toast({
          variant: "destructive",
          title: "Network Error",
          description: "Cannot connect to the server. Please check your connection.",
        });
        return;
      }
      
      toast({
        variant: "destructive",
        title: "Error",
        description: `Failed to load messages: ${error.response?.data?.message || error.message}`,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const fetchMessages = async (userId: number) => {
    try {
      console.log('Fetching messages for user:', userId);
      console.log('Current user when fetching messages:', currentUser);
      const response = await messagesApi.getMessagesForConversation(userId);
      console.log('Messages response:', response);
      console.log('Messages data:', response.data);
      setMessages(response.data);
    } catch (error: any) {
      console.error('Error fetching messages:', error);
      console.error('Error details:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status,
        statusText: error.response?.statusText
      });
      
      toast({
        variant: "destructive",
        title: "Error",
        description: `Failed to load messages: ${error.response?.data?.message || error.message}`,
      });
    }
  };

  const onSubmit = async (data: MessageFormData) => {
    try {
      await messagesApi.sendMessage({
        recipientId: data.recipientId,
        subject: data.subject,
        content: data.content,
      });
      
      form.reset();
      setIsDialogOpen(false);
      fetchData();
      
      toast({
        title: "Success",
        description: "Message sent successfully",
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to send message",
      });
    }
  };

  const sendQuickMessage = async () => {
    if (!newMessage.trim() || !selectedConversation || isSending) return;
    
    setIsSending(true);
    try {
      console.log('Sending message:', {
        recipientId: selectedConversation.userId,
        subject: "Quick message",
        content: newMessage.trim(),
        currentUser: currentUser
      });
      
      const response = await messagesApi.sendMessage({
        recipientId: selectedConversation.userId,
        subject: "Quick message",
        content: newMessage.trim(),
      });
      
      console.log('Message sent response:', response);
      
      setNewMessage("");
      // Refresh messages for the current conversation
      await fetchMessages(selectedConversation.userId);
      // Refresh conversations list
      await fetchData();
      
      toast({
        title: "Success",
        description: "Message sent successfully",
      });
    } catch (error) {
      console.error('Error sending message:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to send message",
      });
    } finally {
      setIsSending(false);
    }
  };

  const filteredConversations = conversations.filter(conversation =>
    conversation.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    conversation.lastMessage?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInHours = (now.getTime() - date.getTime()) / (1000 * 60 * 60);
    
    if (diffInHours < 24) {
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } else if (diffInHours < 168) { // 7 days
      return date.toLocaleDateString([], { weekday: 'short' });
    } else {
      return date.toLocaleDateString([], { month: 'short', day: 'numeric' });
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-pulse-soft text-center">
          <div className="w-12 h-12 bg-gradient-primary rounded-full mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading messages...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-dark">
      <div className="space-y-8 p-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
            <h1 className="text-4xl font-bold mb-2 bg-gradient-primary bg-clip-text text-transparent">
              Messages
            </h1>
            <p className="text-muted-foreground text-lg">
            Communicate with other users and instructors
          </p>
        </div>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button variant="hero" size="lg">
              <Plus className="w-5 h-5 mr-2" />
              New Message
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-md bg-gradient-card border-border shadow-elevated">
            <DialogHeader>
              <DialogTitle className="text-xl font-bold text-foreground">Send New Message</DialogTitle>
              <DialogDescription className="text-muted-foreground">
                Send a message to another user or instructor
              </DialogDescription>
            </DialogHeader>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <div>
                <Label htmlFor="recipientId">Recipient</Label>
                <Select
                  value={form.watch("recipientId")?.toString()}
                  onValueChange={(value) => form.setValue("recipientId", parseInt(value))}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select recipient" />
                  </SelectTrigger>
                  <SelectContent>
                    {users.map((user) => (
                      <SelectItem key={user.userId} value={user.userId.toString()}>
                        {user.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                {form.formState.errors.recipientId && (
                  <p className="text-sm text-destructive mt-1">
                    {form.formState.errors.recipientId.message}
                  </p>
                )}
              </div>

              <div>
                <Label htmlFor="subject">Subject</Label>
                <Input
                  id="subject"
                  {...form.register("subject")}
                  placeholder="Enter message subject"
                />
                {form.formState.errors.subject && (
                  <p className="text-sm text-destructive mt-1">
                    {form.formState.errors.subject.message}
                  </p>
                )}
              </div>

              <div>
                <Label htmlFor="content">Message</Label>
                <Textarea
                  id="content"
                  {...form.register("content")}
                  placeholder="Type your message here..."
                  rows={4}
                />
                {form.formState.errors.content && (
                  <p className="text-sm text-destructive mt-1">
                    {form.formState.errors.content.message}
                  </p>
                )}
              </div>

              <Button type="submit" variant="hero" className="w-full">
                <Send className="w-4 h-4 mr-2" />
                Send Message
              </Button>
            </form>
          </DialogContent>
        </Dialog>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 h-[calc(100vh-200px)] min-h-[700px]">
        {/* Conversations List */}
        <div className="lg:col-span-1">
          <Card className="h-full flex flex-col bg-gradient-card border-border shadow-card">
            <CardHeader className="flex-shrink-0 border-b border-primary/20 bg-gradient-to-r from-primary/10 to-primary/5">
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-primary/20">
                  <MessageCircle className="w-6 h-6 text-primary" />
                </div>
                <CardTitle className="text-xl font-bold text-foreground">Messages</CardTitle>
              </div>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-primary" />
                <Input
                  placeholder="Search conversations..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 bg-card/50 border-primary/30 focus:border-primary/60 focus:ring-primary/20 text-foreground placeholder:text-muted-foreground"
                />
              </div>
            </CardHeader>
            <CardContent className="p-0 flex-1 overflow-hidden">
              <div className="h-full overflow-y-auto">
                {filteredConversations.length > 0 ? (
                  filteredConversations.map((conversation) => (
                    <div
                      key={conversation.userId}
                      className={`p-4 cursor-pointer hover:bg-primary/10 transition-all duration-200 border-b border-border/30 group ${
                        selectedConversation?.userId === conversation.userId 
                          ? 'bg-primary/20 border-l-4 border-l-primary shadow-card' 
                          : 'hover:shadow-subtle'
                      }`}
                      onClick={() => setSelectedConversation(conversation)}
                    >
                      <div className="flex items-center gap-3">
                        <div className="relative">
                          <Avatar className="w-12 h-12 ring-2 ring-primary/30 shadow-subtle">
                            <AvatarImage src={conversation.avatarUrl} alt={conversation.username} />
                            <AvatarFallback className="bg-gradient-primary text-white text-sm font-bold">
                              {conversation.username.split(' ').map(n => n[0]).join('')}
                          </AvatarFallback>
                        </Avatar>
                          {/* Online indicator */}
                          <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-primary border-2 border-card rounded-full shadow-subtle"></div>
                        </div>
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center justify-between mb-1">
                            <h3 className="font-semibold text-sm truncate text-foreground">
                              {conversation.username}
                            </h3>
                            <span className="text-xs text-muted-foreground flex-shrink-0">
                              {conversation.lastMessageTime && formatDate(conversation.lastMessageTime)}
                            </span>
                          </div>
                          <div className="flex items-center justify-between">
                            <p className="text-xs text-muted-foreground truncate flex-1">
                            {conversation.lastMessage || "No messages yet"}
                          </p>
                            {conversation.unread && (
                              <div className="ml-2 flex-shrink-0">
                                <div className="w-2 h-2 bg-primary rounded-full shadow-subtle"></div>
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="p-8 text-center">
                    <div className="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mx-auto mb-4">
                      <MessageCircle className="w-8 h-8 text-primary" />
                    </div>
                    <p className="text-muted-foreground text-lg">
                      {conversations.length === 0 
                        ? "No conversations yet. Send your first message!"
                        : "No conversations match your search."
                      }
                    </p>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Messages */}
        <div className="lg:col-span-2">
          <Card className="h-full flex flex-col bg-gradient-card border-border shadow-card">
            {selectedConversation ? (
              <>
                {/* Chat Header */}
                <CardHeader className="flex-shrink-0 border-b border-primary/20 bg-gradient-to-r from-primary/10 to-primary/5">
                  <div className="flex items-center gap-4">
                    <div className="relative">
                      <Avatar className="w-14 h-14 ring-2 ring-primary/30 shadow-subtle">
                        <AvatarImage src={selectedConversation.avatarUrl} alt={selectedConversation.username} />
                        <AvatarFallback className="bg-gradient-primary text-white font-bold text-lg">
                          {selectedConversation.username.split(' ').map(n => n[0]).join('')}
                      </AvatarFallback>
                    </Avatar>
                      {/* Online indicator */}
                      <div className="absolute -bottom-1 -right-1 w-5 h-5 bg-primary border-2 border-card rounded-full shadow-subtle"></div>
                    </div>
                    <div className="flex-1">
                      <CardTitle className="text-xl font-bold text-foreground">{selectedConversation.username}</CardTitle>
                      <CardDescription className="flex items-center gap-2 text-primary">
                        <div className="w-2 h-2 bg-primary rounded-full shadow-subtle"></div>
                        <span className="font-medium">Online</span>
                      </CardDescription>
                    </div>
                  </div>
                </CardHeader>

                {/* Messages Container */}
                <div className="flex-1 flex flex-col min-h-0">
                  {/* Messages Area with Abstract Background */}
                  <div className="flex-1 overflow-y-auto p-6 space-y-4 bg-gradient-to-b from-card via-card/95 to-secondary/20 relative">
                    {/* Abstract Pattern Overlay */}
                    <div className="absolute inset-0 opacity-[0.03] pointer-events-none">
                      <div className="absolute top-0 left-0 w-full h-full bg-[radial-gradient(circle_at_20%_50%,_rgba(34,197,94,0.3)_0%,_transparent_50%)]"></div>
                      <div className="absolute top-0 right-0 w-full h-full bg-[radial-gradient(circle_at_80%_20%,_rgba(34,197,94,0.2)_0%,_transparent_50%)]"></div>
                      <div className="absolute bottom-0 left-0 w-full h-full bg-[radial-gradient(circle_at_40%_80%,_rgba(34,197,94,0.2)_0%,_transparent_50%)]"></div>
                    </div>
                    {!currentUser ? (
                      <div className="text-center py-8">
                        <div className="animate-pulse-soft text-center">
                          <div className="w-8 h-8 bg-gradient-primary rounded-full mx-auto mb-4"></div>
                          <p className="text-muted-foreground">Loading user data...</p>
                        </div>
                      </div>
                    ) : messages.length > 0 ? (
                      messages.map((message) => {
                        // Determine if message is from current user
                        const isFromCurrentUser = currentUser ? message.senderId === currentUser.id : false;
                        
                        return (
                          <div
                            key={message.id}
                            className={`flex ${isFromCurrentUser ? 'justify-end' : 'justify-start'} group relative`}
                          >
                            <div className={`flex items-end gap-2 max-w-[75%] ${isFromCurrentUser ? 'flex-row-reverse' : 'flex-row'}`}>
                               {/* Avatar - only show for received messages */}
                               {!isFromCurrentUser && (
                                 <Avatar className="w-10 h-10 flex-shrink-0 opacity-0 group-hover:opacity-100 transition-opacity duration-200 ring-2 ring-primary/30 shadow-subtle">
                                   <AvatarImage src={selectedConversation.avatarUrl} alt={selectedConversation.username} />
                                   <AvatarFallback className="bg-gradient-primary text-white text-sm font-bold">
                                     {selectedConversation.username.split(' ').map(n => n[0]).join('')}
                                   </AvatarFallback>
                                 </Avatar>
                               )}
                              
                              {/* Message bubble */}
                              <div className={`flex flex-col ${isFromCurrentUser ? 'items-end' : 'items-start'}`}>
                                {/* Message content */}
                                <div
                                  className={`relative px-5 py-4 rounded-2xl transition-all duration-200 hover:shadow-card ${
                                    isFromCurrentUser
                                      ? 'bg-gradient-primary text-white rounded-br-md shadow-subtle'
                                      : 'bg-card border border-primary/20 text-foreground rounded-bl-md hover:border-primary/40 hover:shadow-subtle'
                                  }`}
                                >
                                  {/* Subject line for formal messages */}
                                  {message.subject && message.subject !== "Quick message" && (
                                    <div className={`text-xs font-medium mb-2 ${
                                      isFromCurrentUser ? 'text-primary-foreground/80' : 'text-muted-foreground'
                                    }`}>
                                      {message.subject}
                                    </div>
                                  )}
                                  
                                  {/* Message content */}
                                  <p className="text-sm leading-relaxed whitespace-pre-wrap font-medium">
                                    {message.content}
                                  </p>
                                  
                                  {/* Message tail */}
                                  <div
                                    className={`absolute bottom-0 w-3 h-3 ${
                                      isFromCurrentUser
                                        ? 'right-0 bg-primary transform translate-x-1/2 rotate-45'
                                        : 'left-0 bg-card border-l border-b border-primary/20 transform -translate-x-1/2 rotate-45'
                                    }`}
                                  />
                                </div>
                                
                                {/* Timestamp and status */}
                                <div className={`flex items-center gap-1 mt-1 px-1 ${
                                  isFromCurrentUser ? 'justify-end' : 'justify-start'
                                }`}>
                                  <span className="text-xs text-muted-foreground">
                                {formatDate(message.sentAt)}
                              </span>
                                   {isFromCurrentUser && (
                                     <div className="flex items-center gap-1">
                                       {message.readAt ? (
                                         <div className="flex">
                                           <span className="text-xs text-primary font-bold">✓✓</span>
                                         </div>
                                       ) : (
                                         <span className="text-xs text-muted-foreground">✓</span>
                                       )}
                                     </div>
                                   )}
                                </div>
                              </div>
                            </div>
                          </div>
                        );
                      })
                     ) : (
                       <div className="flex items-center justify-center h-full">
                         <div className="text-center">
                           <div className="w-20 h-20 bg-primary/20 rounded-full flex items-center justify-center mx-auto mb-6">
                             <Mail className="w-10 h-10 text-primary" />
                        </div>
                           <h3 className="text-xl font-bold mb-3 text-foreground">No messages yet</h3>
                           <p className="text-muted-foreground text-lg">
                             Start the conversation by sending a message
                           </p>
                         </div>
                      </div>
                    )}
                  </div>
                  
                   {/* Message Input - Fixed at bottom */}
                   <div className="flex-shrink-0 p-6 border-t border-border bg-gradient-card backdrop-blur-sm sticky bottom-0 z-10">
                     <div className="flex items-end gap-4">
                       <div className="flex-1 relative">
                         <Textarea
                           placeholder="Write your message..."
                           className="min-h-[50px] max-h-32 resize-none rounded-2xl border-border bg-card shadow-subtle focus:ring-2 focus:ring-primary/20 focus:border-primary/40 pr-12 transition-all duration-200 text-foreground placeholder:text-muted-foreground"
                           value={newMessage}
                           onChange={(e) => setNewMessage(e.target.value)}
                           onKeyDown={(e) => {
                             if (e.key === 'Enter' && !e.shiftKey) {
                               e.preventDefault();
                               if (newMessage.trim()) {
                                 sendQuickMessage();
                               }
                             }
                           }}
                           disabled={isSending}
                           rows={1}
                           style={{ 
                             height: 'auto',
                             minHeight: '50px',
                             maxHeight: '128px'
                           }}
                         />
                         <div className="absolute right-3 bottom-3 text-xs text-muted-foreground">
                           {newMessage.length > 0 && `${newMessage.length} chars`}
                         </div>
                       </div>
                       <Button 
                         onClick={sendQuickMessage}
                         disabled={!newMessage.trim() || isSending}
                         size="sm"
                         className="h-12 w-12 rounded-full bg-gradient-primary hover:shadow-elevated disabled:opacity-50 disabled:cursor-not-allowed flex-shrink-0"
                       >
                         {isSending ? (
                           <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                         ) : (
                           <Send className="w-5 h-5 text-white" />
                         )}
                      </Button>
                     </div>
                     <div className="flex items-center justify-between mt-3 text-xs text-muted-foreground">
                       <span>Press Enter to send, Shift+Enter for new line</span>
                       <span className="flex items-center gap-2">
                         <div className="w-2 h-2 bg-primary rounded-full shadow-subtle"></div>
                         <span className="font-medium">Connected</span>
                       </span>
                     </div>
                    </div>
                  </div>
              </>
            ) : (
              <div className="flex-1 flex items-center justify-center">
                <div className="text-center max-w-md mx-auto p-8">
                  <div className="w-24 h-24 bg-gradient-primary rounded-full flex items-center justify-center mx-auto mb-8 shadow-elevated">
                    <MessageCircle className="w-12 h-12 text-white" />
                  </div>
                  <h3 className="text-2xl font-bold mb-4 text-foreground">Welcome to Messages</h3>
                  <p className="text-muted-foreground mb-8 leading-relaxed text-lg">
                    Select a conversation from the sidebar to start chatting, or send a new message to begin a conversation.
                  </p>
                  <Button 
                    onClick={() => setIsDialogOpen(true)}
                    variant="hero"
                    size="lg"
                  >
                    <Plus className="w-5 h-5 mr-2" />
                    Start New Conversation
                  </Button>
                </div>
              </div>
            )}
          </Card>
        </div>
        </div>
      </div>
    </div>
  );
};
