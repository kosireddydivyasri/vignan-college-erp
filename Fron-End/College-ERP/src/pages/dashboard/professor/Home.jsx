import { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardBody, Typography } from "@material-tailwind/react";

export function Home() {
  const [professor, setProfessor] = useState(null);
  useEffect(() => { const id=localStorage.getItem("professorId"); if(id) axios.get(`http://localhost:8080/api/professors/${id}`).then(({data})=>setProfessor(data)).catch(()=>{}); }, []);
  return <div className="mt-8 space-y-6"><Card><CardBody><Typography variant="h4">Professor Dashboard</Typography><Typography className="mt-1" color="blue-gray">{professor ? `Welcome, ${professor.name}.` : "Welcome to Vignan College ERP."}</Typography></CardBody></Card><div className="grid gap-4 md:grid-cols-3"><Card><CardBody><Typography variant="small">Department</Typography><Typography variant="h6">{professor?.departmentName || "-"}</Typography></CardBody></Card><Card><CardBody><Typography variant="small">Primary Subject</Typography><Typography variant="h6">{professor?.subject || "-"}</Typography></CardBody></Card><Card><CardBody><Typography variant="small">Professor ID</Typography><Typography variant="h6">{professor?.professorId || "-"}</Typography></CardBody></Card></div></div>;
}
export default Home;
