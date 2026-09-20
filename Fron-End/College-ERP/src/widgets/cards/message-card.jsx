import PropTypes from "prop-types";
import { Typography } from "@material-tailwind/react";

export function MessageCard({ name, message, action }) {
  return (
    <div className="flex items-center justify-between gap-4">
      <div className="flex items-center gap-4">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-blue-500 text-sm font-bold text-white">{name.charAt(0).toUpperCase()}</div>
        <div>
          <Typography
            variant="small"
            color="blue-gray"
            className="mb-1 font-semibold"
          >
            {name}
          </Typography>
          <Typography className="text-xs font-normal text-blue-gray-400">
            {message}
          </Typography>
        </div>
      </div>
      {action}
    </div>
  );
}

MessageCard.defaultProps = {
  action: null,
};

MessageCard.propTypes = {
  name: PropTypes.string.isRequired,
  message: PropTypes.node.isRequired,
  action: PropTypes.node,
};

MessageCard.displayName = "/src/widgets/cards/message-card.jsx";

export default MessageCard;
