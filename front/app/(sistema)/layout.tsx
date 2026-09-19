import Footer from "../components/Footer";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
export default function SistemaLayout({children}) {
    return ( 

      <div className="flex min-h-screen bg-[#0a0a0f]">

        <Sidebar/>
       
       <div className="flex-1 flex flex-col min-w-0">
        <Header/>
        <main className="flex-1">
        {children}
        </main>

        <Footer/>
       </div>
    
    </div>);
  }